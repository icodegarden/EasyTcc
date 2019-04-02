package framework.easytcc.transaction;

/**
 * @author Fangfang.Xu
 *
 */
public interface LocalTransaction extends Transaction{
	
	String getParentId();
	
	void addLocalParticipant(LocalParticipant localParticipant);
	
	void markedAsRecovering();
	
	void incrScheduleRetryTimes();
	
	int getScheduleRetryTimes();
}
