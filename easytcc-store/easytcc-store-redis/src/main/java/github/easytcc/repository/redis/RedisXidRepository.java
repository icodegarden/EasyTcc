package github.easytcc.repository.redis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import github.easytcc.configuration.TccProperties;
import github.easytcc.context.Xid;
import github.easytcc.repository.XidRepository;
import github.easytcc.transaction.TransactionStatus;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

/**
 * @author Fangfang.Xu
 *
 */
public class RedisXidRepository extends AbstractRedisRepository implements XidRepository {

	public static final String XID_HASH_KEY_ACTION = "action";
	public static final String XID_HASH_KEY_STATUS = "status";
	public static final String XID_HASH_KEY_CREATEDMILLIS = "createdMillis";

	public static final String KEY_XID_PREFIX = "xid:";

	public RedisXidRepository(RedisResource redisResource, TccProperties tccProperties) {
		super(redisResource, tccProperties);
	}

	public static String generateXidKey(String xid) {
		return KEY_XID_PREFIX + xid;
	}

	/**
	 * xid key = "xid:"+id
	 */
	@Override
	public void createXid(Xid xid) {
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();
			String generateXidKey = generateXidKey(xid.id());
			Map<String, String> kvs = new HashMap<String, String>();
			kvs.put(XID_HASH_KEY_ACTION, xid.action());
			kvs.put(XID_HASH_KEY_STATUS, xid.getTransactionStatus().getStatus());
			kvs.put(XID_HASH_KEY_CREATEDMILLIS, System.currentTimeMillis() + "");
			jedis.hmset(generateXidKey, kvs);
		} finally {
			close(jedis);
		}
	}

	@Override
	public List<Xid> findAllUseless(int createSecondsBefore) {
		Jedis jedis = null;
		List<Response<Map<String, String>>> responses;
		try {
			jedis = redisResource.getResource();
			
			//use set to remove scan duplicates
			Set<String> keyset = new HashSet<String>();
			
			ScanParams scanParams = new ScanParams();
			scanParams.match(KEY_XID_PREFIX + "*");
			scanParams.count(100);
			
			String cursor = "0";
			do {
				ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
				cursor = scanResult.getStringCursor();
				List<String> keys = scanResult.getResult();
				if(keys != null && !keys.isEmpty()) {
					keyset.addAll(keys);					
				}
			}while(!"0".equals(cursor));
			
			if (keyset.isEmpty()) {
				return Collections.EMPTY_LIST;
			}
			String[] keys = keyset.toArray(new String[keyset.size()]);
			Pipeline pipeline = jedis.pipelined();
			try {
				pipeline.multi();

				responses = new ArrayList<Response<Map<String, String>>>(keys.length);

				for (String id : keys) {
					Response<Map<String, String>> hash = pipeline.hgetAll(id);
					responses.add(hash);
				}
				pipeline.exec();
			} finally {
				close(pipeline);
			}

			List<Xid> xids = new LinkedList<Xid>();
			long now = System.currentTimeMillis();
			for (int i = 0, j = responses.size(); i < j; i++) {
				Map<String, String> hash = responses.get(i).get();
				// check create before
				if (Long.parseLong(hash.get(XID_HASH_KEY_CREATEDMILLIS)) + createSecondsBefore * 1000 < now) {
					// check whether has localtransaction in field
					boolean useless = true;
					for (Entry<String, String> entry : hash.entrySet()) {
						if (entry.getKey().startsWith(RedisLocalTransactionRepository.KEY_TRANSACTION_PREFIX)) {
							useless = false;
						}
					}
					if (useless) {
						// return real xid string
						Xid xid = new Xid(keys[i].replace(KEY_XID_PREFIX, ""), hash.get(XID_HASH_KEY_ACTION));
						xid.setTransactionStatus(TransactionStatus.get(hash.get(XID_HASH_KEY_STATUS)));
						xids.add(xid);
					}
				}
			}
			return xids;
		} finally {
			close(jedis);
		}
	}

	@Override
	public TransactionStatus findXidTransactionStatus(String xid) {
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();
			String generateXidKey = generateXidKey(xid);
			String status = jedis.hget(generateXidKey, XID_HASH_KEY_STATUS);
			return TransactionStatus.get(status);
		} finally {
			close(jedis);
		}
	}

	@Override
	public void deleteXid(String xid) {
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();
			String generateXidKey = generateXidKey(xid);
			jedis.del(generateXidKey);
		} finally {
			close(jedis);
		}
	}

	@Override
	public void deleteXids(List<String> xids) {
		if (xids == null || xids.isEmpty()) {
			return;
		}
		String[] generateXidKeys = new String[xids.size()];
		for (int i = 0, j = xids.size(); i < j; i++) {
			generateXidKeys[i] = generateXidKey(xids.get(i));
		}
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();
			jedis.del(generateXidKeys);
		} finally {
			close(jedis);
		}
	}

	@Override
	public void updateToCommit(String xid) {
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();
			jedis.hset(generateXidKey(xid), XID_HASH_KEY_STATUS, TransactionStatus.COMMIT.getStatus());
		} finally {
			close(jedis);
		}
	}

	@Override
	public void updateToRollback(String xid) {
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();
			jedis.hset(generateXidKey(xid), XID_HASH_KEY_STATUS, TransactionStatus.ROLLBACK.getStatus());
		} finally {
			close(jedis);
		}
	}

}
