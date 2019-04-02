package framework.easytcc.repository.redis.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import framework.easytcc.factory.ExtensionLoader;
import framework.easytcc.repository.redis.DefaultRedisResource;
import framework.easytcc.repository.redis.RedisResource;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Configuration
public class RedisResourceConfiguration {
	
	@Bean
	public RedisResource redisResource(Environment environment) {
		return ExtensionLoader.getExtension(RedisResource.class, new DefaultRedisResource(environment));
	}
}
