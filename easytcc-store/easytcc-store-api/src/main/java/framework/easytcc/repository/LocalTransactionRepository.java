package framework.easytcc.repository;

import java.util.List;

import framework.easytcc.transaction.LocalTransaction;

/**
 * @author Fangfang.Xu
 *
 */
public interface LocalTransactionRepository {
	/**
	 * save LocalTransaction,add to xid transactionId key,register to retrying
	 * @param localTransaction
	 */
	void saveAndRegisterRetry(LocalTransaction localTransaction);
	
	void save(LocalTransaction localTransaction);
	
	/**
	 * @param xid
	 * @return	exists values else empty
	 */
	List<LocalTransaction> findLocalTransactions(String xid);
	/**
	 * @param createSecondsBefore	find by createSecondsBefore 
	 * @return
	 */
	List<LocalTransaction> findRetryLocalTransactions(int createSecondsBefore);
	
	void deleteAndRemoveRetry(LocalTransaction localTransaction);
	
	/**
	 * remove transaction from retrying
	 * add xid to recoveryFailedXid collection
	 * @param xid
	 */
	void addToRecoveryFailed(LocalTransaction localTransaction);
	
}
