package framework.easytcc.task;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import framework.easytcc.configuration.TccProperties;
import framework.easytcc.context.Xid;
import framework.easytcc.repository.LockRepository;
import framework.easytcc.repository.LockRepository.LockResult;
import framework.easytcc.repository.XidRepository;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Configuration
public class ClearXidTask {

	static Logger logger = LoggerFactory.getLogger(ClearXidTask.class);

	private final String lockName = "clear-xid-lock";

	@Autowired
	private XidRepository xidRepository;
	@Autowired
	private LockRepository lockRepository;
	@Autowired
	private TccProperties tccProperties;

	@PostConstruct
	public void init() throws Exception {
		if (tccProperties.isRecoveryEnabled()) {
			logger.info("start clear Useless task");
			Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					clear();
				}
			}, tccProperties.getClearXidInitialDelaySeconds(), tccProperties.getClearXidPeriodSeconds(),
					TimeUnit.SECONDS);
		}
	}

	public void clear() {
		LockResult lockResult = null;
		try {
			lockResult = lockRepository.acquireLockNonBlock(lockName,
					tccProperties.getClearXidPeriodSeconds() * 2 * 1000);
			if (lockResult.isAcquire()) {
				List<Xid> xids = xidRepository.findAllUseless(tccProperties.getClearXidBeforeSeconds());
				if (logger.isInfoEnabled()) {
					logger.info("Useless xids found :{}", xids.size());
				}
				int clearfailed = 0;
				for (Xid xid : xids) {
					try {
						xidRepository.deleteXid(xid.id());
					} catch (Exception e) {
						clearfailed++;
						logger.error("clear Useless xids error,xid:{}", xid.id(), e);
					}
				}
				logger.info("clear Useless xids success:{},failed:{}", xids.size() - clearfailed, clearfailed);
			}
		} catch (Exception e) {
			logger.error("clear Possibly Useless xids error", e);
		} finally {
			if (lockResult != null) {
				lockRepository.releaseLock(lockResult);
			}
		}
	}
}
