package framework.easytcc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import framework.easytcc.configuration.TccProperties;
import framework.easytcc.context.TransactionContext;
import framework.easytcc.context.TransactionContextHolder;
import framework.easytcc.context.Xid;
import framework.easytcc.remoting.TransactionChannel;
import framework.easytcc.repository.LocalTransactionRepository;
import framework.easytcc.repository.TransactionDownstreamRepository;
import framework.easytcc.repository.XidRepository;
import framework.easytcc.transaction.LocalParticipant;
import framework.easytcc.transaction.LocalTransaction;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class TransactionInterceptor implements TransactionAspectInterceptor {

	static Logger logger = LoggerFactory.getLogger(TransactionInterceptor.class);

	private XidRepository xidRepository;

	private LocalTransactionRepository localTransactionRepository;

	private TransactionDownstreamRepository transactionDownstreamRepository;

	private TransactionChannel transactionChannel;

	TccProperties tccProperties;

	public TransactionInterceptor(XidRepository xidRepository, LocalTransactionRepository localTransactionRepository,
			TransactionDownstreamRepository transactionDownstreamRepository, TransactionChannel transactionChannel,
			TccProperties tccProperties) {
		this.xidRepository = xidRepository;
		this.localTransactionRepository = localTransactionRepository;
		this.transactionDownstreamRepository = transactionDownstreamRepository;
		this.transactionChannel = transactionChannel;
		this.tccProperties = tccProperties;
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public void preHandle(ProceedingJoinPointWrapper pointWrapper, ExecutionChain chain) throws Exception {
		TransactionContext transactionContext = TransactionContextHolder.getContext();
		if (transactionContext.getParentId() != null) {
			// downstream
			transactionDownstreamRepository.addAssociatedDownStreamApplication(transactionContext.getParentId(),
					tccProperties.getApplication());
		}
		if (transactionContext.getUpstreamServer() != null) {
			// downstream
			if (transactionChannel != null) {
				transactionChannel.preDownstreamAnnotedMethodExec(transactionContext.getUpstreamServer());
			}
		}

		LocalTransaction localTransaction = transactionContext.getLocalTransaction();

		LocalParticipant localParticipant = LocalParticipant.builder()
				.confirmer(pointWrapper.className(), pointWrapper.confirmMethodName(), pointWrapper.args())
				.canceller(pointWrapper.className(), pointWrapper.cancelMethodName(), pointWrapper.args()).build();

		localTransaction.addLocalParticipant(localParticipant);

		TransactionContextHolder.setContext(transactionContext);

		chain.preHandle(pointWrapper, chain);
	}

	@Override
	public void postHandle(ProceedingJoinPointWrapper pointWrapper) {
		if (pointWrapper.isXidCreateStack()) {
			TransactionContext transactionContext = TransactionContextHolder.getContext();
			Xid xid = transactionContext.getXid();
			LocalTransaction localTransaction = transactionContext.getLocalTransaction();
			boolean xidTransactionSuccess = xidCreateStackCommit(xid, localTransaction);
			pointWrapper.putMetadata("xidTransactionSuccess", xidTransactionSuccess);
		}
	}

	@Override
	public void afterCompletion(ProceedingJoinPointWrapper pointWrapper, Throwable e) {
		Boolean xidTransactionSuccess = (Boolean) pointWrapper.getMetadata("xidTransactionSuccess");
		TransactionContext transactionContext = TransactionContextHolder.getContext();
		Xid xid = transactionContext.getXid();
		LocalTransaction localTransaction = transactionContext.getLocalTransaction();
		if (e != null) {
			if (pointWrapper.isXidCreateStack()) {
				xidTransactionSuccess = xidCreateStackRollback(xid, localTransaction);
			}
		}

		if (pointWrapper.isXidCreateStack()) {
			xidCreateStackFinally(xid, localTransaction, xidTransactionSuccess);
		} else {
			// for downstream
			if (pointWrapper.isLocalTransactionBeginStack()) {
				localTransactionBegineStackFinally(localTransaction);
			}
		}
	}

	private boolean xidCreateStackCommit(Xid xid, LocalTransaction localTransaction) {
		boolean result = localTransaction.commit();
		if (!result) {
			try {
				/**
				 * if commit error,may be network error,the transaction's status should change
				 * to commit
				 */
				xidRepository.updateToCommit(xid.id());
			} catch (Exception e) {
				logger.error("xid updateToCommit failed,xid:{}", xid.id(), e);
			}
		}
		return result;
	}

	private boolean xidCreateStackRollback(Xid xid, LocalTransaction localTransaction) {
		boolean result = localTransaction.rollback();
		if (!result) {
			try {
				xidRepository.updateToRollback(xid.id());
			} catch (Exception e) {
				logger.error("xid updateToRollback failed,xid:{}", xid.id(), e);
			}
		}
		return result;
	}

	private void xidCreateStackFinally(Xid xid, LocalTransaction localTransaction, boolean xidTransactionSuccess) {
		TransactionContextHolder.clearContext();
		if (xidTransactionSuccess) {
			try {
				xidRepository.deleteXid(xid.id());
			} catch (Exception e) {
				// ignore
			}
		} else {
			try {
				localTransactionRepository.saveAndRegisterRetry(localTransaction);
			} catch (Exception e) {
				logger.error("xidCreaterFinally saveAndRegister localTransaction error,xid:{},localTransaction.id:{}",
						localTransaction.getXid(), localTransaction.getTransactionId(), e);
			}
		}
	}

	private void localTransactionBegineStackFinally(LocalTransaction localTransaction) {
		TransactionContextHolder.clearContext();
		try {
			localTransactionRepository.saveAndRegisterRetry(localTransaction);
		} catch (Exception e) {
			logger.error(
					"localTransactionBeginerFinally saveAndRegister localTransaction error,xid:{},localTransaction.id:{}",
					localTransaction.getXid(), localTransaction.getTransactionId(), e);
		}
	}
}
