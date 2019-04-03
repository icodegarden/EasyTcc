package github.easytcc.remoting;

import github.easytcc.transaction.TransactionStatus;

/**
 * @author Fangfang.Xu
 *
 */
public interface TransactionChannel {
	
	/**
	 * exec before down stream server's tcc annoted method
	 * The purpose of setting up this method is prepare do something,such as open topic to receive commit/rollback when use kafka
	 * @param parentApplication	the name from upstream server
	 */
	void preDownstreamAnnotedMethodExec(String upstreamApplication);

	boolean sendTransaction(String application,String xid,TransactionStatus transactionStatus);
}