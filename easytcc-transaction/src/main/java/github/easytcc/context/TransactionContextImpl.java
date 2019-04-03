package github.easytcc.context;

import github.easytcc.transaction.LocalTransaction;

/**
 * @author Fangfang.Xu
 *
 */
public class TransactionContextImpl implements TransactionContext {
	
	private Xid xid;
	private String upstreamServer;
	private String parentId;
	private LocalTransaction localTransaction;

	public TransactionContextImpl(Xid xid,String upstreamServer,String parentId){
		if(xid == null){
			throw new NullPointerException("construct TransactionContext xid can not be null");
		}
		this.xid = xid;
		this.upstreamServer = upstreamServer;
		this.parentId = parentId;
	}
	
	@Override
	public Xid getXid() {
		return xid;
	}
	
	@Override
	public String getUpstreamServer() {
		return upstreamServer;
	}
	
	@Override
	public String getParentId() {
		return parentId;
	}

	@Override
	public LocalTransaction getLocalTransaction() {
		return localTransaction;
	}
	
	@Override
	public void setLocalTransaction(LocalTransaction localTransaction) {
		this.localTransaction = localTransaction;
	}
}
