package github.easytcc;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import github.easytcc.configuration.TccProperties;
import github.easytcc.context.TransactionContext;
import github.easytcc.context.TransactionContextHolder;
import github.easytcc.context.TransactionContextImpl;
import github.easytcc.context.Xid;
import github.easytcc.exception.TccException;
import github.easytcc.repository.XidRepository;
import github.easytcc.transaction.LocalTransaction;
import github.easytcc.transaction.LocalTransactionImpl;

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

	@Pointcut("@annotation(github.easytcc.annotation.EasyTcc)")
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
