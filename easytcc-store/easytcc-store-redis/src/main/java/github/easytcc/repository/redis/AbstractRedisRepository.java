package github.easytcc.repository.redis;


import github.easytcc.configuration.TccProperties;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * @author Fangfang.Xu
 *
 */
public abstract class AbstractRedisRepository {
	
	protected RedisResource redisResource;
	
	protected TccProperties tccProperties;
	
	protected AbstractRedisRepository(RedisResource redisResource,TccProperties tccProperties) {
		this.redisResource = redisResource;
		this.tccProperties = tccProperties;
	}
	
	protected void close(Jedis jedis){
		redisResource.close(jedis);
	}
	
	protected void close(Pipeline pipeline){
		redisResource.close(pipeline);
	}
}
