package github.easytcc.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author Fangfang.Xu
 */
@Configuration
public class TccProperties {

	static Logger logger = LoggerFactory.getLogger(TccProperties.class);

	static final String PREFIX = "easytcc";

	@Value("${spring.application.name}")
	private String application;

	@Value("${" + PREFIX + ".transaction.remote.realtime:true}")
	private boolean realtime;
	
	@Value("${" + PREFIX + ".transaction.handle.threadpool.corePoolSize:15}")
	private int transactionHandleCorePoolSize;
	
	@Value("${" + PREFIX + ".transaction.threadpool.maxPoolSize:30}")
	private int transactionHandleMaxPoolSize;
	
	@Value("${" + PREFIX + ".transaction.threadpool.keepAliveSeconds:60}")
	private int transactionHandlekeepAliveSeconds;
	
	@Value("${" + PREFIX + ".transaction.threadpool.queueSize:"+Integer.MAX_VALUE+"}")
	private int transactionHandleQueueSize;

	@Value("${" + PREFIX + ".transaction.recovery.enabled:true}")
	private boolean recoveryEnabled;

	@Value("${" + PREFIX + ".transaction.recovery.maxRetrys:5}")
	private int recoveryMaxRetrys;

	@Value("${" + PREFIX + ".transaction.recovery.initialDelaySeconds:60}")
	private int recoveryInitialDelaySeconds;

	@Value("${" + PREFIX + ".transaction.recovery.periodSeconds:30}")
	private int recoveryPeriodSeconds;

	@Value("${" + PREFIX + ".transaction.recovery.createdBeforeSeconds:60}")
	private int recoveryBeforeSeconds;

	@Value("${" + PREFIX + ".transaction.clearXid.initialDelaySeconds:60}")
	private int clearXidInitialDelaySeconds;

	@Value("${" + PREFIX + ".transaction.clearXid.periodSeconds:600}")
	private int clearXidPeriodSeconds;

	@Value("${" + PREFIX + ".transaction.clearXid.createdBeforeSeconds:1800}")
	private int clearXidBeforeSeconds;

	@Value("${" + PREFIX + ".metrics.enabled:true}")
	private boolean metricsEnabled;

	public String getApplication() {
		return application;
	}

	public boolean isRealtime() {
		return realtime;
	}

	public boolean isRecoveryEnabled() {
		return recoveryEnabled;
	}

	public int getRecoveryMaxRetrys() {
		return recoveryMaxRetrys;
	}

	public int getRecoveryInitialDelaySeconds() {
		return recoveryInitialDelaySeconds;
	}

	public int getRecoveryPeriodSeconds() {
		return recoveryPeriodSeconds;
	}

	public int getRecoveryBeforeSeconds() {
		return recoveryBeforeSeconds;
	}

	public int getClearXidInitialDelaySeconds() {
		return clearXidInitialDelaySeconds;
	}

	public int getClearXidPeriodSeconds() {
		return clearXidPeriodSeconds;
	}

	public int getClearXidBeforeSeconds() {
		return clearXidBeforeSeconds;
	}

	public boolean isMetricsEnabled() {
		return metricsEnabled;
	}

	public int getTransactionHandleCorePoolSize() {
		return transactionHandleCorePoolSize;
	}

	public int getTransactionHandleMaxPoolSize() {
		return transactionHandleMaxPoolSize;
	}

	public int getTransactionHandlekeepAliveSeconds() {
		return transactionHandlekeepAliveSeconds;
	}

	public int getTransactionHandleQueueSize() {
		return transactionHandleQueueSize;
	}

}
