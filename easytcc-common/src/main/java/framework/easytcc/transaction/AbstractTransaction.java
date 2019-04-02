package framework.easytcc.transaction;

import java.util.UUID;

/**
 * @author Fangfang.Xu
 *
 */
public abstract class AbstractTransaction implements Transaction{
	
	private static final long serialVersionUID = 1L;

	private String xid;
	
	private String transactionId;
	
	public AbstractTransaction(String xid){
		this.xid = xid;
		this.transactionId = UUID.randomUUID().toString();
	}
	
	@Override
	public String getXid() {
		return this.xid;
	}
	
	@Override
	public String getTransactionId() {
		return transactionId;
	}
}
