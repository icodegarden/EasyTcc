package github.easytcc.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import github.easytcc.AspectExecutionChain;
import github.easytcc.TransactionAspect;
import github.easytcc.configuration.TccProperties;
import github.easytcc.repository.XidRepository;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Configuration
public class AspectConfiguration {

	@Bean
	public TransactionAspect transactionAspect(AspectExecutionChain chain, XidRepository xidRepository,
			TccProperties tccProperties) {
		return new TransactionAspect(chain, xidRepository, tccProperties);
	}
}
