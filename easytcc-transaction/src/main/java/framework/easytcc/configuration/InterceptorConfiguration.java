package framework.easytcc.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import framework.easytcc.AspectExecutionChain;
import framework.easytcc.MetricsInterceptor;
import framework.easytcc.TransactionAspectInterceptor;
import framework.easytcc.TransactionInterceptor;
import framework.easytcc.remoting.TransactionChannel;
import framework.easytcc.repository.LocalTransactionRepository;
import framework.easytcc.repository.TransactionDownstreamRepository;
import framework.easytcc.repository.XidRepository;

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
		return new MetricsInterceptor();
	}
}
