package github.easytcc.remoting;

import github.easytcc.transaction.TransactionStatus;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class NoOpTransactionChannel implements TransactionChannel {
	@Override
	public void preDownstreamAnnotedMethodExec(String upstreamApplication) {
	}

	@Override
	public boolean sendTransaction(String application, String xid, TransactionStatus transactionStatus) {
		return false;
	}
}