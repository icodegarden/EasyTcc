package github.easytcc.repository.redis;

import java.util.Collection;

import github.easytcc.configuration.TccProperties;
import github.easytcc.repository.TransactionDownstreamRepository;
import redis.clients.jedis.Jedis;

/**
 * @author Fangfang.Xu
 *
 */
public class RedisTransactionDownstreamRepository extends AbstractRedisRepository implements TransactionDownstreamRepository {

	private static final String KEY_TRANSACTION_DOWNSTREAM_PREFIX = "transaction-downstreams:";

	public RedisTransactionDownstreamRepository(RedisResource redisResource,TccProperties tccProperties) {
		super(redisResource,tccProperties);
	}
	
	public void addAssociatedDownStreamApplication(String transactionId, String application) {
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();
			jedis.sadd(KEY_TRANSACTION_DOWNSTREAM_PREFIX + transactionId, application);
		} finally {
			redisResource.close(jedis);
		}
	}

	public Collection<String> getAssociatedDownStreamApplications(String transactionId) {
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();
			return jedis.smembers(KEY_TRANSACTION_DOWNSTREAM_PREFIX + transactionId);
		} finally {
			redisResource.close(jedis);
		}
	}

	@Override
	public void deleteAssociatedDownStreamApplications(String transactionId) {
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();
			jedis.del(KEY_TRANSACTION_DOWNSTREAM_PREFIX + transactionId);
		} finally {
			redisResource.close(jedis);
		}
	}
}
