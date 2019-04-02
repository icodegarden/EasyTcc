package framework.easytcc.repository.redis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import framework.easytcc.configuration.TccProperties;
import framework.easytcc.context.Xid;
import framework.easytcc.repository.XidRepository;
import framework.easytcc.transaction.TransactionStatus;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

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
			Set<String> keyset = jedis.keys(KEY_XID_PREFIX + "*");
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
						//return real xid string
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
