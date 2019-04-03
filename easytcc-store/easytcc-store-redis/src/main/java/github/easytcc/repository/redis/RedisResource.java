package github.easytcc.repository.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * @author Fangfang.Xu
 *
 */
public interface RedisResource {
	
	public Jedis getResource();
	
	void close(Jedis jedis);
	
	void close(Pipeline pipeline);
}
