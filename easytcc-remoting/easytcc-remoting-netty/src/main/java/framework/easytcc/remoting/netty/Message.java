package framework.easytcc.remoting.netty;

import java.io.Serializable;

import framework.easytcc.transaction.TransactionStatus;

/**
 * @author Fangfang.Xu
 *
 */
class Message implements Serializable{

	private static final long serialVersionUID = 1L;

	private String xid;
	
	private TransactionStatus transactionStatus;
	
	public Message() {
		super();
	}

	public Message(String xid, TransactionStatus transactionStatus) {
		super();
		this.xid = xid;
		this.transactionStatus = transactionStatus;
	}

	public String getXid() {
		return xid;
	}

	public void setXid(String xid) {
		this.xid = xid;
	}

	public TransactionStatus getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(TransactionStatus transactionStatus) {
		this.transactionStatus = transactionStatus;
	}
	
	
}
