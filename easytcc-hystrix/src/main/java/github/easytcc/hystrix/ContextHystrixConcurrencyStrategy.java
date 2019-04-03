package github.easytcc.hystrix;

import java.util.concurrent.Callable;

import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;

import github.easytcc.context.TransactionContext;
import github.easytcc.context.TransactionContextHolder;
/**
 * transfer TransactionContext to hystrix threadpool 
 * @author Fangfang.Xu
 *
 */
public class ContextHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {
	
	private HystrixConcurrencyStrategy existingHystrixConcurrencyStrategy;

	/**
	 * Decorator 
	 * @param existingHystrixConcurrencyStrategy	can be null
	 */
    public ContextHystrixConcurrencyStrategy(HystrixConcurrencyStrategy existingHystrixConcurrencyStrategy) {
    	this.existingHystrixConcurrencyStrategy = existingHystrixConcurrencyStrategy;
    }

    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
    	return existingHystrixConcurrencyStrategy != null
				? existingHystrixConcurrencyStrategy
						.wrapCallable(new DelegatingHystrixConcurrencyCallable<T>(callable))
				: super.wrapCallable(new DelegatingHystrixConcurrencyCallable<T>(callable));
    }

    public static class DelegatingHystrixConcurrencyCallable<V> implements Callable<V> {
        private Callable<V> delegate;
        
        private TransactionContext transactionContext;

        public DelegatingHystrixConcurrencyCallable(Callable<V> delegate) {
        	TransactionContext transactionContext = TransactionContextHolder.getContext();
        	if(this.transactionContext == null){
        		this.transactionContext = transactionContext;
        	}
            this.delegate = delegate;
        }
        
        @Override
        public V call() throws Exception {
        	TransactionContext originalTransactionContext = TransactionContextHolder.getContext();
        	TransactionContextHolder.setContext(this.transactionContext);
           	try{
           		return this.delegate.call();
           	}finally {
           		TransactionContextHolder.setContext(originalTransactionContext);
			}
        }
    }
}