package github.easytcc.repository.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import github.easytcc.configuration.TccProperties;
import github.easytcc.repository.LocalTransactionRepository;
import github.easytcc.transaction.LocalTransaction;
import github.easytcc.transaction.Transaction;
import github.easytcc.util.SerializationUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

/**
 * @author Fangfang.Xu
 *
 */
public class RedisLocalTransactionRepository extends AbstractRedisRepository implements LocalTransactionRepository {

	private static final String EMPTY = "";

	public static final String KEY_TRANSACTION_PREFIX = "localtransaction:";

	private static final String KEY_TRANSACTION_RETRY_PREFIX = "retry-localtransactions:";

	private static final String KEY_RECOVERY_FAILED_XIDS = "recovery-failed-xids";

	public RedisLocalTransactionRepository(RedisResource redisResource, TccProperties tccProperties) {
		super(redisResource, tccProperties);
	}

	@Override
	public void saveAndRegisterRetry(LocalTransaction localTransaction) {
		Jedis jedis = null;
		Pipeline pipeline = null;
		try {
			jedis = redisResource.getResource();
			pipeline = jedis.pipelined();
			pipeline.multi();
			String generateTransactionKey = generateTransactionKey(localTransaction);
			// save
			pipeline.set(generateTransactionKey.getBytes(), SerializationUtils.seriaObject(localTransaction));
			pipeline.hsetnx(RedisXidRepository.generateXidKey(localTransaction.getXid()), generateTransactionKey, EMPTY);
			// register
			pipeline.zadd(generateServerRetryKey(), System.currentTimeMillis(), generateTransactionKey);

			pipeline.exec();
		} finally {
			close(pipeline);
			close(jedis);
		}
	}

	@Override
	public void save(LocalTransaction localTransaction) {
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();
			jedis.set(generateTransactionKey(localTransaction).getBytes(),
					SerializationUtils.seriaObject(localTransaction));
		} finally {
			close(jedis);
		}
	}

	@Override
	public List<LocalTransaction> findLocalTransactions(String xid) {
		Jedis jedis = null;
		Map<String, String> all = null;
		try {
			jedis = redisResource.getResource();
			all = jedis.hgetAll(RedisXidRepository.generateXidKey(xid));
			if (all == null || all.isEmpty()) {
				return Collections.EMPTY_LIST;
			}
		} finally {
			close(jedis);
		}
		
		List<LocalTransaction> resultList = new LinkedList<LocalTransaction>();

		String localTransactionKeyPrefix = generateLocalTransactionKeyPrefix();
		for (Map.Entry<String, String> entry : all.entrySet()) {
			String key = entry.getKey();
			if (key.startsWith(localTransactionKeyPrefix)) {
				byte[] bs = jedis.get(key.getBytes());
				if (bs != null) {
					LocalTransaction localTransaction = (LocalTransaction) SerializationUtils.deseriaObject(bs);
					resultList.add(localTransaction);
				}
			}
		}
		return resultList;
	}

	@Override
	public List<LocalTransaction> findRetryLocalTransactions(int createSecondsBefore) {
		Jedis jedis = null;
		Pipeline pipeline = null;
		List<Response<byte[]>> responses;
		try {
			jedis = redisResource.getResource();
			Collection<String> retryLocalTransactionKeys = jedis.zrangeByScore(generateServerRetryKey(), 0,
					System.currentTimeMillis() - createSecondsBefore * 1000);
			if (retryLocalTransactionKeys.isEmpty()) {
				return Collections.EMPTY_LIST;
			}
			pipeline = jedis.pipelined();
			try {
				pipeline.multi();

				responses = new ArrayList<Response<byte[]>>(retryLocalTransactionKeys.size());
				for (String retryLocalTransactionKey : retryLocalTransactionKeys) {
					Response<byte[]> response = pipeline.get(retryLocalTransactionKey.getBytes());
					responses.add(response);
				}
				pipeline.exec();
			}finally {
				close(pipeline);					
			}
		} finally {
			close(jedis);
		}
		List<LocalTransaction> localTransactions = new ArrayList<LocalTransaction>(responses.size());
		for (Response<byte[]> response : responses) {
			byte[] bs = response.get();
			LocalTransaction localTransaction = (LocalTransaction) SerializationUtils.deseriaObject(bs);
			localTransactions.add(localTransaction);
		}
		return localTransactions;
	}

	@Override
	public void deleteAndRemoveRetry(LocalTransaction localTransaction) {
		Jedis jedis = null;
		Pipeline pipeline = null;
		try {
			jedis = redisResource.getResource();
			pipeline = jedis.pipelined();
			pipeline.multi();
			String transactionKey = generateTransactionKey(localTransaction);
			pipeline.del(transactionKey);
			pipeline.zrem(generateServerRetryKey(), generateTransactionKey(localTransaction));
			pipeline.hdel(RedisXidRepository.generateXidKey(localTransaction.getXid()), transactionKey);
			pipeline.exec();
		} finally {
			close(pipeline);
			close(jedis);
		}
	}

	@Override
	public void addToRecoveryFailed(LocalTransaction localTransaction) {
		Jedis jedis = null;
		Pipeline pipeline = null;
		try {
			jedis = redisResource.getResource();
			pipeline = jedis.pipelined();
			pipeline.multi();
			pipeline.zrem(generateServerRetryKey(), generateTransactionKey(localTransaction));
			pipeline.zadd(KEY_RECOVERY_FAILED_XIDS, System.nanoTime(),
					RedisXidRepository.generateXidKey(localTransaction.getXid()));

			pipeline.exec();
		} finally {
			close(pipeline);
			close(jedis);
		}
	}

	/**
	 * @param transaction
	 * @return "tk:"+serverName+":"+transactionId
	 */
	private String generateTransactionKey(Transaction transaction) {
		return generateLocalTransactionKeyPrefix() + transaction.getTransactionId();
	}

	private String generateLocalTransactionKeyPrefix() {
		StringBuilder sb = new StringBuilder(KEY_TRANSACTION_PREFIX);
		return sb.append(tccProperties.getApplication()).append(":").toString();
	}

	private String generateServerRetryKey() {
		return KEY_TRANSACTION_RETRY_PREFIX + tccProperties.getApplication();
	}
}
