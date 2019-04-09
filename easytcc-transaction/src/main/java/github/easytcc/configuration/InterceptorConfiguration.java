package github.easytcc.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import github.easytcc.AspectExecutionChain;
import github.easytcc.MetricsInterceptor;
import github.easytcc.TransactionAspectInterceptor;
import github.easytcc.TransactionInterceptor;
import github.easytcc.remoting.TransactionChannel;
import github.easytcc.repository.LocalTransactionRepository;
import github.easytcc.repository.MetricsRepository;
import github.easytcc.repository.TransactionDownstreamRepository;
import github.easytcc.repository.XidRepository;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Configuration
public class InterceptorConfiguration {

	@Autowired(required = false)
	TransactionChannel transactionChannel;
	@Autowired
	TccProperties tccProperties;
	@Autowired
	MetricsRepository metricsRepository;

	@Bean
	public AspectExecutionChain chain(TransactionAspectInterceptor[] interceptors) {
		return new AspectExecutionChain(interceptors);
	}

	@Bean
	public TransactionAspectInterceptor transaction(XidRepository xidRepository,
			LocalTransactionRepository localTransactionRepository,
			TransactionDownstreamRepository transactionDownstreamRepository) {
		return new TransactionInterceptor(xidRepository, localTransactionRepository, transactionDownstreamRepository,
				transactionChannel, tccProperties);
	}

	@Bean
	public TransactionAspectInterceptor metricsInterceptor() {
		return new MetricsInterceptor(metricsRepository);
	}
}
