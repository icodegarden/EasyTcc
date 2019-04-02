package framework.easytcc;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import framework.easytcc.configuration.TccProperties;
import framework.easytcc.context.TransactionContext;
import framework.easytcc.context.TransactionContextHolder;
import framework.easytcc.context.TransactionContextImpl;
import framework.easytcc.context.Xid;
import framework.easytcc.exception.TccException;
import framework.easytcc.repository.XidRepository;
import framework.easytcc.transaction.LocalTransaction;
import framework.easytcc.transaction.LocalTransactionImpl;

/**
 * @author Fangfang.Xu
 */
@Aspect
public class TransactionAspect {

	static Logger logger = LoggerFactory.getLogger(TransactionAspect.class);

	@Autowired
	AspectExecutionChain chain;
	@Autowired
	XidRepository xidRepository;
	@Autowired
	TccProperties tccProperties;

	@Pointcut("@annotation(framework.easytcc.annotation.EasyTcc)")
	public void pointcut() {
	}

	@Around("pointcut()")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		ProceedingJoinPointWrapper pointWrapper = new ProceedingJoinPointWrapper(pjp);

		TransactionContext transactionContext = TransactionContextHolder.getContext();
		if (transactionContext == null) {
			pointWrapper.markeXidCreateStack();

			Xid xid = Xid.newInstance(pointWrapper.action());
			xidRepository.createXid(xid);

			transactionContext = new TransactionContextImpl(xid, null, null);
			TransactionContextHolder.setContext(transactionContext);
		}

		LocalTransaction localTransaction = transactionContext.getLocalTransaction();
		if (localTransaction == null) {
			pointWrapper.markeLocalTransactionBeginStack();

			Xid xid = transactionContext.getXid();
			localTransaction = new LocalTransactionImpl(xid.id(), transactionContext.getParentId(),
					tccProperties.isRemoteSync());

			transactionContext.setLocalTransaction(localTransaction);
		}

		try {
			chain.applyPreHandle(pointWrapper);
		} catch (Exception e) {
			throw new TccException("tcc ExecutionChain PreHandle error,cause : {} " + e.getMessage(), e);
		}
		Throwable ex = null;
		try {
			Object result = pjp.proceed();

			try {
				chain.applyPostHandle(pointWrapper);
			} catch (Exception e) {
				logger.error("transaction chain post handle error", e);
			}

			return result;
		} catch (Throwable e) {
			ex = e;
			throw e;
		} finally {
			try {
				chain.triggerAfterHandleCompletion(pointWrapper, ex);
			} catch (Exception e) {
				logger.error("transaction chain after completion handle error", e);
			}
		}
	}
}
