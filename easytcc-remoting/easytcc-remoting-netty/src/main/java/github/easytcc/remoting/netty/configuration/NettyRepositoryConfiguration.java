package github.easytcc.remoting.netty.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import github.easytcc.factory.ExtensionLoader;
import github.easytcc.repository.redis.RedisResource;
import github.easytcc.remoting.netty.repository.NettyRepository;
import github.easytcc.remoting.netty.repository.RedisNettyRepository;

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
