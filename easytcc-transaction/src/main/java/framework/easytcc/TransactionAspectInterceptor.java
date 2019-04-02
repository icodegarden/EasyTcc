package framework.easytcc;

import org.springframework.core.Ordered;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public interface TransactionAspectInterceptor extends Ordered{

	void preHandle(ProceedingJoinPointWrapper pointWrapper,ExecutionChain chain) throws Exception;

	void postHandle(ProceedingJoinPointWrapper pointWrapper);

	void afterCompletion(ProceedingJoinPointWrapper pointWrapper, Throwable e);

}
