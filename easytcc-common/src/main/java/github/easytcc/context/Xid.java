package github.easytcc.context;

import java.util.UUID;

import github.easytcc.transaction.TransactionStatus;

/**
 * @author Fangfang.Xu
 *
 */
public class Xid {
	
	private String id;
	
	private String action;
	
	private TransactionStatus transactionStatus;
	
	public static Xid newInstance(String action){
		Xid xid = new Xid(UUID.randomUUID().toString(),action);
		xid.setTransactionStatus(TransactionStatus.BEGIN);
		return xid;
	}
	
	public Xid(String id,String action){
		if(id == null){
			throw new NullPointerException("construct parameter id can not be null");
		}
		this.id = id;
		this.action = action;
	}

	public String id() {
		return id;
	}
	public String action() {
		return action;
	}

	public void setTransactionStatus(TransactionStatus transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public TransactionStatus getTransactionStatus() {
		return transactionStatus;
	}
	
}
