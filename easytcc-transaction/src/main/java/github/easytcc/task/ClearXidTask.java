package github.easytcc.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import github.easytcc.configuration.TccProperties;
import github.easytcc.context.Xid;
import github.easytcc.repository.LockRepository;
import github.easytcc.repository.XidRepository;
import github.easytcc.repository.LockRepository.LockResult;

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
				if (logger.isDebugEnabled()) {
					logger.debug("found Useless xids :{}", xids.size());
				}
				List<String> ids = new ArrayList<String>(xids.size());
				for(Xid xid:xids) {
					ids.add(xid.id());
				}
				xidRepository.deleteXids(ids);
				if (logger.isDebugEnabled()) {
					logger.debug("clear Useless xids :{}", xids.size());					
				}
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
