package framework.easytcc.remoting.netty.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import framework.easytcc.factory.ExtensionLoader;
import framework.easytcc.remoting.netty.repository.NettyRepository;
import framework.easytcc.remoting.netty.repository.RedisNettyRepository;
import framework.easytcc.repository.redis.RedisResource;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Configuration
public class NettyRepositoryConfiguration {

	@Autowired(required = false)
	RedisResource redisResource;

	@Bean
	public NettyRepository nettyRepository(NettyProperties nettyProperties) {
		return ExtensionLoader.getExtension(NettyRepository.class,
				new RedisNettyRepository(redisResource, nettyProperties));
	}

}
