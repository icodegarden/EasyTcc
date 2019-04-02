package framework.easytcc.repository.redis.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import framework.easytcc.configuration.TccProperties;
import framework.easytcc.factory.ExtensionLoader;
import framework.easytcc.repository.LocalTransactionRepository;
import framework.easytcc.repository.LockRepository;
import framework.easytcc.repository.MetricsRepository;
import framework.easytcc.repository.TransactionDownstreamRepository;
import framework.easytcc.repository.XidRepository;
import framework.easytcc.repository.redis.RedisLocalTransactionRepository;
import framework.easytcc.repository.redis.RedisLockRepository;
import framework.easytcc.repository.redis.RedisMetricsRepository;
import framework.easytcc.repository.redis.RedisResource;
import framework.easytcc.repository.redis.RedisTransactionDownstreamRepository;
import framework.easytcc.repository.redis.RedisXidRepository;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Configuration
public class RepositoryConfiguration {

	@Autowired(required = false)
	RedisResource redisResource;
	@Autowired
	TccProperties tccProperties;
	
	@Bean
	public LockRepository lockRepository() {
		return ExtensionLoader.getExtension(LockRepository.class, new RedisLockRepository(redisResource, tccProperties));
	}

	@Bean
	public XidRepository xidRepository() {
		return ExtensionLoader.getExtension(XidRepository.class, new RedisXidRepository(redisResource, tccProperties));
	}

	@Bean
	public LocalTransactionRepository localTransactionRepository() {
		return ExtensionLoader.getExtension(LocalTransactionRepository.class,
				new RedisLocalTransactionRepository(redisResource, tccProperties));
	}

	@Bean
	public TransactionDownstreamRepository transactionDownstreamRepository() {
		return ExtensionLoader.getExtension(TransactionDownstreamRepository.class,
				new RedisTransactionDownstreamRepository(redisResource, tccProperties));
	}

	@Bean
	public MetricsRepository metricsRepository() {
		return ExtensionLoader.getExtension(MetricsRepository.class,
				new RedisMetricsRepository(redisResource, tccProperties));
	}
}
