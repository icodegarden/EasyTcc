package framework.easytcc.remoting.netty.repository;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import framework.easytcc.remoting.netty.configuration.NettyProperties;
import framework.easytcc.repository.redis.AbstractRedisRepository;
import framework.easytcc.repository.redis.RedisResource;
import redis.clients.jedis.Jedis;

/**
 * @author Fangfang.Xu
 *
 */
public class RedisNettyRepository extends AbstractRedisRepository implements NettyRepository {

	static Logger logger = LoggerFactory.getLogger(RedisNettyRepository.class);

	private static final String KEY_NETTY_SERVER_PREFIX = "nettyserver:";

	private static final String KEY_NETTY_SERVER_PREFIX2 = "@";

	NettyProperties nettyProperties;
	
	public RedisNettyRepository(RedisResource redisResource,NettyProperties nettyProperties) {
		super(redisResource,nettyProperties.getTccProperties());
		this.nettyProperties = nettyProperties;
	}

	@Override
	public void addServer(String address, int port, long expireMills) {
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();
			// value is connections count
			jedis.set(generateNettyServerKey(address, port), "0", "NX", "PX", expireMills);
		} finally {
			redisResource.close(jedis);
		}
	}

	@Override
	public void updateExpire(String address, int port, long expireMills) {
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();
			jedis.pexpire(generateNettyServerKey(address, port), expireMills);
		} finally {
			redisResource.close(jedis);
		}
	}

	@Override
	public void incrConnection(String address, int port) {
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();
			jedis.incr(generateNettyServerKey(address, port));
		} finally {
			redisResource.close(jedis);
		}
	}

	@Override
	public void decrConnection(String address, int port) {
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();
			jedis.decr(generateNettyServerKey(address, port));
		} finally {
			redisResource.close(jedis);
		}
	}

	@Override
	public String getSuitableServer(String applicationName) {
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();

			Set<String> keys = jedis.keys(KEY_NETTY_SERVER_PREFIX + applicationName + KEY_NETTY_SERVER_PREFIX2 + "*");
			if (keys == null || keys.isEmpty()) {
				return null;
			}
			String[] arrayKeys = keys.toArray(new String[keys.size()]);
			List<String> connections = jedis.mget(arrayKeys);
			int minConnection = 0;
			int minConnectionIndex = 0;
			for (int i = 0, j = connections.size(); i < j; i++) {
				int connection = Integer.parseInt(connections.get(i));
				if (connection <= minConnection) {
					minConnection = connection;
					minConnectionIndex = i;
				}
			}
			return arrayKeys[minConnectionIndex].split(KEY_NETTY_SERVER_PREFIX2)[1];
		} finally {
			redisResource.close(jedis);
		}
	}

	@Override
	public void removeServer(String applicationName, String address, int port) {
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();
			jedis.del(generateNettyServerKey(address, port));
		} finally {
			redisResource.close(jedis);
		}
	}

	private String generateNettyServerKey(String address, int port) {
		return KEY_NETTY_SERVER_PREFIX + nettyProperties.getTccProperties().getApplication() + KEY_NETTY_SERVER_PREFIX2 + address
				+ ":" + port;
	}
}
