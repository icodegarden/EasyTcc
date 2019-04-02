package framework.easytcc.transaction;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import framework.easytcc.configuration.TccProperties;
import framework.easytcc.exception.NeverBeRecoveryException;
import framework.easytcc.factory.SpringBeanFactory;
import framework.easytcc.repository.factory.RepositoryFactory;

/**
 * @author Fangfang.Xu
 *
 */
public class LocalTransactionImpl extends AbstractTransaction implements LocalTransaction{
	
	private static final long serialVersionUID = 1L;
	
	static Logger logger = LoggerFactory.getLogger(LocalTransactionImpl.class);
	
	private String parentId;
	
	private static final int capcity = 5;
	
	private LocalParticipant[] localParticipants = new LocalParticipant[capcity];
	/**
	 * {@link participants} size
	 */
	private int size;
	
	private int scheduleRetryTimes;
	
	private RemoteTransaction remoteTransaction;
	/**
	 * remoteTransaction will ignore if isRecovering was marked true
	 */
	private boolean isRecovering;
	
	public LocalTransactionImpl(String xid,String parentId,boolean remoteSync){
		super(xid);
		this.parentId = parentId;
		if(remoteSync) {
			remoteTransaction = new RemoteTransaction(xid, this.getTransactionId());
		}else {
			remoteTransaction = new NonOpRemoteTransaction(xid, this.getTransactionId());
		}
	}
	
	@Override
	public String getParentId() {
		return parentId;
	}
	
	@Override
	public void addLocalParticipant(LocalParticipant localParticipant) {
		if(size == localParticipants.length){
			LocalParticipant[] dest = new LocalParticipant[capcity + localParticipants.length];
			System.arraycopy(localParticipants, 0, dest, 0, localParticipants.length);
			localParticipants = dest;			
		}
		localParticipants[size++] = localParticipant;
	}
	
	@Override
	public boolean commit() {
		boolean result = true;
		if(remoteTransaction != null && !isRecovering){
			result = remoteTransaction.commit();
		}
		try {
			List<LocalParticipant> participants = getLocalParticipants();
			for(LocalParticipant participant:participants){
				participant.confirm();
			}
			try{
				RepositoryFactory.getLocalTransactionRepository().deleteAndRemoveRetry(this);				
			}catch (Exception e) {
				//ignore
			}
		}catch (NeverBeRecoveryException e) {
			//can not recovery,so remove from retrying, move associate xid to recoveryFailedXid collection
			try{
				RepositoryFactory.getLocalTransactionRepository().addToRecoveryFailed(this);
			}catch (Exception e1) {
				//ignore
			}
		}catch (Exception e) {
			result = false;
			logger.error("commit local transaction failed",e);
		}finally {
			try{
				deleteAssociatedDownStreamApplications();
			} catch (Exception e) {
				// ignore
			}
			if(!result) {
				try{
					incrScheduleRetryTimes();
				}catch (Exception e) {
					//ignore
				}
			}
		}
		return result;
	}

	@Override
	public boolean rollback() {
		boolean result = true;
		if(remoteTransaction != null && !isRecovering){
			result = remoteTransaction.rollback();
		}
		try {
			List<LocalParticipant> participants = getLocalParticipants();
			for(LocalParticipant participant:participants){
				participant.cancel();
			} 
			try{
				RepositoryFactory.getLocalTransactionRepository().deleteAndRemoveRetry(this);	
			}catch (Exception e) {
				//ignore
			}
		}catch (NeverBeRecoveryException e) {
			//can not recovery,so remove from retrying, move associate xid to recoveryFailedXid collection
			try{
				RepositoryFactory.getLocalTransactionRepository().addToRecoveryFailed(this);
			}catch (Exception e1) {
				//ignore
			}
		}catch (Exception e) {
			result = false;
			logger.error("rollback local transaction failed",e);
		}finally {
			try{
				deleteAssociatedDownStreamApplications();
			} catch (Exception e) {
				// ignore
			}
			if(!result) {
				try{
					incrScheduleRetryTimes();
				}catch (Exception e) {
					//ignore
				}
			}
		}
		return result;
	}

	private List<LocalParticipant> getLocalParticipants() {
		List<LocalParticipant> list = new ArrayList<LocalParticipant>(localParticipants.length);
		for(LocalParticipant participant:localParticipants){
			if(participant!=null){
				list.add(participant);
			}
		}
		return list;
	}
	
	@Override
	public void markedAsRecovering() {
		isRecovering = true;
		remoteTransaction = null;
	}

	@Override
	public void incrScheduleRetryTimes() {
		scheduleRetryTimes++;
		TccProperties tccProperties = SpringBeanFactory.getBean(TccProperties.class);
		if(scheduleRetryTimes >= tccProperties.getRecoveryMaxRetrys()){
			RepositoryFactory.getLocalTransactionRepository().addToRecoveryFailed(this);
			logger.warn("localTransaction.scheduleRetryTimes reached setted max times:{} but still failed so add to recovery failed collection,"
					+ "serverName:{},localTransaction.id:{}",getScheduleRetryTimes(),
					tccProperties.getApplication(),getTransactionId());
		}else{
			RepositoryFactory.getLocalTransactionRepository().save(this);			
		}
	}

	@Override
	public int getScheduleRetryTimes() {
		return scheduleRetryTimes;
	}
	
	private void deleteAssociatedDownStreamApplications() {
		if(getParentId() != null) {
			RepositoryFactory.getTransactionDownstreamRepository()
					.deleteAssociatedDownStreamApplications(getParentId());
			this.parentId = null;
		}
	}
}
