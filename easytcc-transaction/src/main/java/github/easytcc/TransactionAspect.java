package github.easytcc;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	AspectExecutionChain chain;

	XidRepository xidRepository;

	TccProperties tccProperties;

	ThreadPoolExecutor realtimeHandleThreadPool;

	public TransactionAspect(AspectExecutionChain chain, XidRepository xidRepository,
			final TccProperties tccProperties) {
		this.chain = chain;
		this.xidRepository = xidRepository;
		this.tccProperties = tccProperties;
		realtimeHandleThreadPool = new ThreadPoolExecutor(tccProperties.getTransactionHandleCorePoolSize(),
				tccProperties.getTransactionHandleMaxPoolSize(), tccProperties.getTransactionHandlekeepAliveSeconds(),
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(tccProperties.getTransactionHandleQueueSize()),
				new ThreadFactory() {
					ThreadGroup mGroup;
					protected final AtomicInteger mThreadNum = new AtomicInteger(1);
					{
						SecurityManager s = System.getSecurityManager();
						mGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
					}

					@Override
					public Thread newThread(Runnable r) {
						String name = "EasyTcc-transaction-thread-" + mThreadNum.getAndIncrement();
						Thread ret = new Thread(mGroup, r, name, 0);
						ret.setDaemon(false);
						return ret;
					}
				}, new RejectedExecutionHandler() {
					@Override
					public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
						throw new RejectedExecutionException("realtimeHandleThreadPool Task " + r.toString()
								+ " rejected from " + e.toString() + ",transactionHandleMaxPoolSize "
								+ tccProperties.getTransactionHandleMaxPoolSize() + ",transactionHandleQueueSize "
								+ tccProperties.getTransactionHandleQueueSize());
					}
				});
	}

	@Pointcut("@annotation(github.easytcc.annotation.EasyTcc)")
	public void pointcut() {
	}

	@Around("pointcut()")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		final ProceedingJoinPointWrapper pointWrapper = new ProceedingJoinPointWrapper(pjp);

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
					tccProperties.isRealtime());

			transactionContext.setLocalTransaction(localTransaction);
		}

		try {
			chain.applyPreHandle(pointWrapper);
		} catch (Exception e) {
			throw new TccException("tcc ExecutionChain PreHandle error,cause : {} " + e.getMessage(), e);
		}
		try {
			Object result = pjp.proceed();

			realtimeHandleThreadPool.execute(new AfterProceedSuccessRunnable(transactionContext, pointWrapper));

			return result;
		} catch (final Throwable e) {
			realtimeHandleThreadPool.execute(new AfterProceedFailedRunnable(transactionContext, pointWrapper, e));
			throw e;
		}
	}

	class AfterProceedSuccessRunnable implements Runnable {
		TransactionContext transactionContext;
		ProceedingJoinPointWrapper pointWrapper;

		AfterProceedSuccessRunnable(TransactionContext transactionContext, ProceedingJoinPointWrapper pointWrapper) {
			this.transactionContext = transactionContext;
			this.pointWrapper = pointWrapper;
		}

		@Override
		public void run() {
			TransactionContextHolder.setContext(transactionContext);
			try {
				chain.applyPostHandle(pointWrapper);
			} catch (Exception e) {
				logger.error("transaction chain post handle error", e);
			}
			try {
				chain.triggerAfterHandleCompletion(pointWrapper, null);
			} catch (Exception e) {
				logger.error("transaction chain after completion handle error", e);
			}
		}
	}

	class AfterProceedFailedRunnable implements Runnable {
		TransactionContext transactionContext;
		ProceedingJoinPointWrapper pointWrapper;
		Throwable e;

		AfterProceedFailedRunnable(TransactionContext transactionContext, ProceedingJoinPointWrapper pointWrapper,
				Throwable e) {
			this.transactionContext = transactionContext;
			this.pointWrapper = pointWrapper;
			this.e = e;
		}

		@Override
		public void run() {
			TransactionContextHolder.setContext(transactionContext);
			try {
				chain.triggerAfterHandleCompletion(pointWrapper, e);
			} catch (Exception e) {
				logger.error("transaction chain after completion handle error", e);
			}
		}
	}
}
