package github.easytcc.repository.factory;

import github.easytcc.factory.SpringBeanFactory;
import github.easytcc.repository.LocalTransactionRepository;
import github.easytcc.repository.MetricsRepository;
import github.easytcc.repository.TransactionDownstreamRepository;
import github.easytcc.repository.XidRepository;

/**
 * @author Fangfang.Xu
 *
 */
public class RepositoryFactory {
	
	static XidRepository xidRepository;
	
	static LocalTransactionRepository localTransactionRepository;
	
	static TransactionDownstreamRepository transactionDownstreamRepository;
	
	static MetricsRepository metricsRepository;
	
	public static XidRepository getXidRepository(){
		if(xidRepository == null) {
			xidRepository = SpringBeanFactory.getBean(XidRepository.class);			
		}
		return xidRepository;
	}
	
	public static LocalTransactionRepository getLocalTransactionRepository(){
		if(localTransactionRepository == null) {
			localTransactionRepository = SpringBeanFactory.getBean(LocalTransactionRepository.class);	
		}
		return localTransactionRepository;
	}
	
	public static TransactionDownstreamRepository getTransactionDownstreamRepository(){
		if(transactionDownstreamRepository == null) {
			transactionDownstreamRepository = SpringBeanFactory.getBean(TransactionDownstreamRepository.class);	
		}
		return transactionDownstreamRepository;
	}
	
	public static MetricsRepository getMetricsRepository(){
		if(metricsRepository == null) {
			metricsRepository = SpringBeanFactory.getBean(MetricsRepository.class);	
		}
		return metricsRepository;
	}
}
