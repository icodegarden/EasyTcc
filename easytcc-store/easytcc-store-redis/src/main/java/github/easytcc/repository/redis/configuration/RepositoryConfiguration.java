package github.easytcc.repository.redis.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import github.easytcc.configuration.TccProperties;
import github.easytcc.factory.ExtensionLoader;
import github.easytcc.repository.LocalTransactionRepository;
import github.easytcc.repository.LockRepository;
import github.easytcc.repository.MetricsRepository;
import github.easytcc.repository.MetricsRepository.NoOpMetricsRepository;
import github.easytcc.repository.TransactionDownstreamRepository;
import github.easytcc.repository.XidRepository;
import github.easytcc.repository.redis.RedisLocalTransactionRepository;
import github.easytcc.repository.redis.RedisLockRepository;
import github.easytcc.repository.redis.RedisMetricsRepository;
import github.easytcc.repository.redis.RedisResource;
import github.easytcc.repository.redis.RedisTransactionDownstreamRepository;
import github.easytcc.repository.redis.RedisXidRepository;

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
		if(!tccProperties.isMetricsEnabled()) {
			return new NoOpMetricsRepository();
		}
		return ExtensionLoader.getExtension(MetricsRepository.class,
				new RedisMetricsRepository(redisResource, tccProperties));
	}
}
