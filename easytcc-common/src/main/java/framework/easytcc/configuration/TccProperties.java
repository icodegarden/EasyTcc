package framework.easytcc.configuration;

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

	@Value("${" + PREFIX + ".transaction.remote.sync:true}")
	private boolean remoteSync;

	@Value("${" + PREFIX + ".recovery.enabled:true}")
	private boolean recoveryEnabled;

	@Value("${" + PREFIX + ".recovery.maxRetrys:5}")
	private int recoveryMaxRetrys;

	@Value("${" + PREFIX + ".recovery.initialDelaySeconds:60}")
	private int recoveryInitialDelaySeconds;

	@Value("${" + PREFIX + ".recovery.periodSeconds:30}")
	private int recoveryPeriodSeconds;

	@Value("${" + PREFIX + ".recovery.beforeSeconds:60}")
	private int recoveryBeforeSeconds;

	@Value("${" + PREFIX + ".clearXid.initialDelaySeconds:60}")
	private int clearXidInitialDelaySeconds;

	@Value("${" + PREFIX + ".clearXid.periodSeconds:3600}")
	private int clearXidPeriodSeconds;

	@Value("${" + PREFIX + ".clearXid.beforeSeconds:3600}")
	private int clearXidBeforeSeconds;

	@Value("${" + PREFIX + ".metrics.enabled:true}")
	private boolean metricsEnabled;

	public String getApplication() {
		return application;
	}

	public boolean isRemoteSync() {
		return remoteSync;
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
}
