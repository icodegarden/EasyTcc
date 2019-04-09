package github.easytcc.repository;

import java.util.List;

import github.easytcc.context.Xid;
import github.easytcc.transaction.TransactionStatus;

/**
 * @author Fangfang.Xu
 *
 */
public interface XidRepository {

	/**
	 * create new xid data
	 * @param xid
	 */
	void createXid(Xid xid);
	
	/**
	 * Useless means create before {@link createSecondsBefore} and didn't had any transactions in it's field
	 * @param createSecondsBefore
	 * @return
	 */
	List<Xid> findAllUseless(int createSecondsBefore);
	
	TransactionStatus findXidTransactionStatus(String xid);
	
	void deleteXid(String xid);
	
	void deleteXids(List<String> xids);
	/**
	 * update status to commit
	 * @param xid
	 */
	void updateToCommit(String xid);
	
	void updateToRollback(String xid);
	
}
