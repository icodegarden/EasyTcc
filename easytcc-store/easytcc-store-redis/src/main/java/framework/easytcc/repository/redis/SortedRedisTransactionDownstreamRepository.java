package framework.easytcc.repository.redis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import framework.easytcc.configuration.TccProperties;
import framework.easytcc.repository.TransactionDownstreamRepository;
import redis.clients.jedis.Jedis;

/**
 * @author Fangfang.Xu
 *
 */
public class SortedRedisTransactionDownstreamRepository extends AbstractRedisRepository implements TransactionDownstreamRepository {

	private static final String KEY_TRANSACTION_DOWNSTREAM_PREFIX = "transaction-downstreams:";

	public SortedRedisTransactionDownstreamRepository(RedisResource redisResource,TccProperties tccProperties) {
		super(redisResource,tccProperties);
	}
	
	public void addAssociatedDownStreamApplication(String transactionId, String application) {
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();
			jedis.rpush(KEY_TRANSACTION_DOWNSTREAM_PREFIX + transactionId, application);
		} finally {
			redisResource.close(jedis);
		}
	}

	public Collection<String> getAssociatedDownStreamApplications(String transactionId) {
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();
			List<String> applications = jedis.lrange(KEY_TRANSACTION_DOWNSTREAM_PREFIX + transactionId, 0, -1);
			List<String> result = new LinkedList<String>();
			for (String application : applications) {
				if (!result.contains(application)) {
					result.add(application);
				}
			}
			return result;
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
