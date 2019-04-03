package github.easytcc.repository.redis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Pipeline;

/**
 * @author Fangfang.Xu
 *
 */
public class DefaultRedisResource implements RedisResource {

	static Logger logger = LoggerFactory.getLogger(DefaultRedisResource.class);

	private static final int DEFAULT_CONNECTION_TIMEOUT = 3000;
	private static final int DEFAULT_SO_TIMEOUT = 3000;

	private static JedisSentinelPool jedisSentinelPool;
	private static JedisPool jedisPool;

	Environment environment;

	public DefaultRedisResource(Environment environment) {
		this.environment = environment;
		initPool();
	}

	private void initPool() {
		String masterName = environment.getProperty("easytcc.jedisSentinelPool.masterName");
		if (StringUtils.hasText(masterName)) {
			String sentinelsString = environment.getProperty("easytcc.jedisSentinelPool.sentinels");
			Set<String> sentinels = new HashSet<String>(Arrays.asList(sentinelsString.split(",")));

			String connectionTimeout = environment.getProperty("easytcc.jedisSentinelPool.connectionTimeout");
			String soTimeout = environment.getProperty("easytcc.jedisSentinelPool.soTimeout");
			String passwordStr = environment.getProperty("easytcc.jedisSentinelPool.password");
			String databaseStr = environment.getProperty("easytcc.jedisSentinelPool.database");

			String maxTotal = environment.getProperty("easytcc.jedisSentinelPool.maxTotal");
			String maxIdle = environment.getProperty("easytcc.jedisSentinelPool.maxIdle");
			String minIdle = environment.getProperty("easytcc.jedisSentinelPool.minIdle");

			GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
			if (maxTotal != null) {
				genericObjectPoolConfig.setMaxTotal(Integer.parseInt(maxTotal));
			}
			if (maxIdle != null) {
				genericObjectPoolConfig.setMaxIdle(Integer.parseInt(maxIdle));
			}
			if (minIdle != null) {
				genericObjectPoolConfig.setMinIdle(Integer.parseInt(minIdle));
			}
			int conTimeout = connectionTimeout != null ? Integer.parseInt(connectionTimeout)
					: DEFAULT_CONNECTION_TIMEOUT;
			int socketTimeout = soTimeout != null ? Integer.parseInt(soTimeout) : DEFAULT_SO_TIMEOUT;
			String password = passwordStr != null && !passwordStr.trim().equals("") ? passwordStr : null;
			int database = databaseStr != null && !databaseStr.trim().equals("") ? Integer.parseInt(databaseStr) : 0;

			logger.info(
					"start inital jedisSentinelPool,masterName:{},sentinels:{},genericObjectPoolConfig:{},"
							+ "conTimeout:{},socketTimeout:{},password:{},database:{}",
					masterName, sentinels, genericObjectPoolConfig, conTimeout, socketTimeout, password, database);

			jedisSentinelPool = new JedisSentinelPool(masterName, sentinels, genericObjectPoolConfig, conTimeout,
					socketTimeout, password, database);
			initalTryGetResource();
		} else {
			String host = environment.getProperty("easytcc.jedisPool.host");
			String port = environment.getProperty("easytcc.jedisPool.port");
			String connectionTimeout = environment.getProperty("easytcc.jedisPool.connectionTimeout");
			String soTimeout = environment.getProperty("easytcc.jedisPool.soTimeout");
			String passwordStr = environment.getProperty("easytcc.jedisPool.password");
			String databaseStr = environment.getProperty("easytcc.jedisPool.database");

			String maxTotal = environment.getProperty("easytcc.jedisPool.maxTotal");
			String maxIdle = environment.getProperty("easytcc.jedisPool.maxIdle");
			String minIdle = environment.getProperty("easytcc.jedisPool.minIdle");

			GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
			if (maxTotal != null) {
				genericObjectPoolConfig.setMaxTotal(Integer.parseInt(maxTotal));
			}
			if (maxIdle != null) {
				genericObjectPoolConfig.setMaxIdle(Integer.parseInt(maxIdle));
			}
			if (minIdle != null) {
				genericObjectPoolConfig.setMinIdle(Integer.parseInt(minIdle));
			}
			int conTimeout = connectionTimeout != null ? Integer.parseInt(connectionTimeout)
					: DEFAULT_CONNECTION_TIMEOUT;
			int socketTimeout = soTimeout != null ? Integer.parseInt(soTimeout) : DEFAULT_SO_TIMEOUT;
			String password = passwordStr != null && !passwordStr.trim().equals("") ? passwordStr : null;
			int database = databaseStr != null && !databaseStr.trim().equals("") ? Integer.parseInt(databaseStr) : 0;

			logger.info(
					"start inital JedisPool,host:{},port:{},"
							+ "conTimeout:{},socketTimeout:{},password:{},database:{},genericObjectPoolConfig:{}",
					host, port, conTimeout, socketTimeout, password, database, genericObjectPoolConfig);

			jedisPool = new JedisPool(genericObjectPoolConfig, host, Integer.parseInt(port), conTimeout, socketTimeout,
					password, database, null, false, null, null, null);
			
			initalTryGetResource();
		}
	}
	//check
	private void initalTryGetResource() {
		Jedis jedis = null;
		try {
			jedis = getResource();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Jedis getResource() {
		if (jedisSentinelPool != null) {
			return jedisSentinelPool.getResource();
		} else {
			return jedisPool.getResource();
		}
	}

	public void close(Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}

	public void close(Pipeline pipeline) {
		if (pipeline != null) {
			try {
				pipeline.close();
			} catch (Exception e) {
				logger.error("close pipeline error", e);
			}
		}
	}
}
