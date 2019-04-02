package framework.easytcc.repository.factory;

import framework.easytcc.factory.SpringBeanFactory;
import framework.easytcc.repository.LocalTransactionRepository;
import framework.easytcc.repository.MetricsRepository;
import framework.easytcc.repository.TransactionDownstreamRepository;
import framework.easytcc.repository.XidRepository;

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
