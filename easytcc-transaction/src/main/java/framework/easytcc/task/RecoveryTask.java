package framework.easytcc.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import framework.easytcc.configuration.TccProperties;
import framework.easytcc.repository.LocalTransactionRepository;
import framework.easytcc.repository.LockRepository;
import framework.easytcc.repository.LockRepository.LockResult;
import framework.easytcc.repository.XidRepository;
import framework.easytcc.transaction.LocalTransaction;
import framework.easytcc.transaction.TransactionStatus;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Configuration
public class RecoveryTask {

	static Logger logger = LoggerFactory.getLogger(RecoveryTask.class);

	private final String recoveryLockPrefix = "recovery-lock:";

	@Autowired
	private XidRepository xidRepository;
	@Autowired
	private LocalTransactionRepository localTransactionRepository;
	@Autowired
	private LockRepository lockRepository;
	@Autowired
	private TccProperties tccProperties;

	@PostConstruct
	public void init() throws Exception {
		if (tccProperties.isRecoveryEnabled()) {
			logger.info("start transaction recovery task");
			Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					recovery();
				}
			}, tccProperties.getRecoveryInitialDelaySeconds(), tccProperties.getRecoveryPeriodSeconds(), TimeUnit.SECONDS);
		}
	}

	private String getLockName() {
		return recoveryLockPrefix + tccProperties.getApplication();
	}

	public void recovery() {
		LockResult lockResult = null;
		try {
			String lockName = getLockName();
			lockResult = lockRepository.acquireLockNonBlock(lockName,
					tccProperties.getRecoveryPeriodSeconds() * 2 * 1000);
			if (lockResult.isAcquire()) {
				List<LocalTransaction> retryLocalTransactions = localTransactionRepository
						.findRetryLocalTransactions(tccProperties.getRecoveryBeforeSeconds());
				if (logger.isInfoEnabled()) {
					logger.info("recovery transactions found :{},current application:{}", retryLocalTransactions.size(),
							tccProperties.getApplication());
				}
				if (!retryLocalTransactions.isEmpty()) {
					//cache, key = xid
					Map<String, TransactionStatus> xidStatuses = new HashMap<String, TransactionStatus>();
					for (LocalTransaction retryLocalTransaction : retryLocalTransactions) {
						try {
							retryLocalTransaction.markedAsRecovering();

							String xid = retryLocalTransaction.getXid();
							TransactionStatus transactionStatus = xidStatuses.get(xid);
							if (transactionStatus == null) {
								transactionStatus = xidRepository.findXidTransactionStatus(xid);
								xidStatuses.put(xid, transactionStatus);
							}
							if (TransactionStatus.COMMIT.equals(transactionStatus)) {
								retryLocalTransaction.commit();
							}
							if (TransactionStatus.ROLLBACK.equals(transactionStatus)) {
								retryLocalTransaction.rollback();
							}
						} catch (Exception e) {
							logger.error("retryLocalTransaction error,serverName:{},localTransaction.id:{}",
									tccProperties.getApplication(), retryLocalTransaction.getTransactionId(), e);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("retryLocalTransaction error,serverName:{}", tccProperties.getApplication(), e);
		} finally {
			if (lockResult != null) {
				lockRepository.releaseLock(lockResult);
			}
		}
	}
}
