package github.easytcc.repository;

import java.util.Collection;

/**
 * @author Fangfang.Xu
 *
 */
public interface TransactionDownstreamRepository {

	void addAssociatedDownStreamApplication(String transactionId, String application);

	Collection<String> getAssociatedDownStreamApplications(String transactionId);

	void deleteAssociatedDownStreamApplications(String transactionId);
}
