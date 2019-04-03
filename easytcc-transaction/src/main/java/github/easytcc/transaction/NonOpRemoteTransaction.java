package github.easytcc.transaction;

/**
 * @author Fangfang.Xu
 *
 */
public class NonOpRemoteTransaction extends RemoteTransaction {

	private static final long serialVersionUID = 1L;

	public NonOpRemoteTransaction(String xid, String localTransactionId) {
		super(xid,localTransactionId);
	}

	@Override
	public boolean commit() {
		return false;
	}

	@Override
	public boolean rollback() {
		return false;
	}
}
