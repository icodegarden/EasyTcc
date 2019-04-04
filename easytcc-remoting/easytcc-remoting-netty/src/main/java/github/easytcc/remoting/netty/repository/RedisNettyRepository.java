package github.easytcc.remoting.netty.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import github.easytcc.repository.redis.AbstractRedisRepository;
import github.easytcc.repository.redis.RedisResource;
import github.easytcc.remoting.netty.configuration.NettyProperties;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * @author Fangfang.Xu
 *
 */
public class RedisNettyRepository extends AbstractRedisRepository implements NettyRepository {

	static Logger logger = LoggerFactory.getLogger(RedisNettyRepository.class);

	private static final String KEY_NETTY_SERVER_PREFIX = "nettyserver:";

	private static final String KEY_NETTY_SERVER_PREFIX2 = "@";
	
	private Random random = new Random();

	NettyProperties nettyProperties;
	
	public RedisNettyRepository(RedisResource redisResource,NettyProperties nettyProperties) {
		super(redisResource,nettyProperties.getTccProperties());
		this.nettyProperties = nettyProperties;
	}

	@Override
	public void addServer(String address, int port,int weight, long expireMills) {
		if(weight < 1 || weight > 100) {
			throw new IllegalArgumentException("netty server weight should between 1 and 100");
		}
		Jedis jedis = null;
		Pipeline pipeline = null;
		try {
			jedis = redisResource.getResource();
			pipeline = jedis.pipelined();
			String key = generateNettyServerKey(address, port);
			pipeline.multi();
			pipeline.set(key, weight + "");
			pipeline.pexpire(key, expireMills);
			pipeline.exec();
		} finally {
			close(pipeline);
			close(jedis);
		}
	}

	@Override
	public void updateExpire(String address, int port, long expireMills) {
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();
			jedis.pexpire(generateNettyServerKey(address, port), expireMills);
		} finally {
			close(jedis);
		}
	}

	@Override
	public void incrConnection(String address, int port) {
		//
	}

	@Override
	public void decrConnection(String address, int port) {
		//
	}

	@Override
	public String getSuitableServer(String applicationName) {
		Jedis jedis = null;
		String[] serverKeys;
		List<String> weights;
		try {
			jedis = redisResource.getResource();

			Set<String> keys = jedis.keys(KEY_NETTY_SERVER_PREFIX + applicationName + KEY_NETTY_SERVER_PREFIX2 + "*");
			if (keys == null || keys.isEmpty()) {
				return null;
			}
			serverKeys = keys.toArray(new String[keys.size()]);
			weights = jedis.mget(serverKeys);
		} finally {
			close(jedis);
		}
		return weightedRandom(serverKeys, weights).split(KEY_NETTY_SERVER_PREFIX2)[1];
	}
	
	private String weightedRandom(String[] serverKeys,List<String> weights) {
		List<String> serverKeyList = new ArrayList<String>((serverKeys.length * 50));
		for (int i = 0, j = serverKeys.length; i < j; i++) {
			String serverKey = serverKeys[i];
			int weight = Integer.parseInt(weights.get(i));
			
			for (int g = 0; g < weight; g++) {
				serverKeyList.add(serverKey);
			}
		}
		return serverKeyList.get(random.nextInt(serverKeyList.size()));
	}

	@Override
	public void removeServer(String applicationName, String address, int port) {
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();
			jedis.del(generateNettyServerKey(address, port));
		} finally {
			close(jedis);
		}
	}

	private String generateNettyServerKey(String address, int port) {
		return KEY_NETTY_SERVER_PREFIX + nettyProperties.getTccProperties().getApplication() + KEY_NETTY_SERVER_PREFIX2 + address
				+ ":" + port;
	}
}
