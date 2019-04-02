package framework.easytcc.transaction;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import framework.easytcc.remoting.factory.TransactionChannelFactory;
import framework.easytcc.repository.factory.RepositoryFactory;

/**
 * @author Fangfang.Xu
 *
 */
public class RemoteTransaction extends AbstractTransaction {

	private static final long serialVersionUID = 1L;

	static Logger logger = LoggerFactory.getLogger(RemoteTransaction.class);

	private String localTransactionId;

	public RemoteTransaction(String xid, String localTransactionId) {
		super(xid);
		this.localTransactionId = localTransactionId;
	}

	@Override
	public boolean commit() {
		boolean result = true;
		try {
			Collection<String> applications = RepositoryFactory.getTransactionDownstreamRepository()
					.getAssociatedDownStreamApplications(localTransactionId);
			if (logger.isDebugEnabled()) {
				logger.debug("RemoteTransaction commit,applications : {}", applications);
			}
			if (applications != null) {
				for (String application : applications) {
					boolean success = TransactionChannelFactory.getTransactionChannel().sendTransaction(application,
							getXid(), TransactionStatus.COMMIT);
					if (!success) {
						result = false;
					}
				}
			}
			return result;
		} catch (Exception e) {
			logger.error("remote commit failed", e);
			return false;
		}
	}

	@Override
	public boolean rollback() {
		boolean result = true;
		try {
			Collection<String> applications = RepositoryFactory.getTransactionDownstreamRepository()
					.getAssociatedDownStreamApplications(localTransactionId);
			if (logger.isDebugEnabled()) {
				logger.debug("RemoteTransaction rollback,applications : {}", applications);
			}
			if (applications != null) {
				for (String application : applications) {
					boolean success = TransactionChannelFactory.getTransactionChannel().sendTransaction(application,
							getXid(), TransactionStatus.ROLLBACK);
					if (!success) {
						result = false;
					}
				}
			}
			return result;
		} catch (Exception e) {
			logger.error("remote rollback failed", e);
			return false;
		}
	}
}
