package github.easytcc.metrics;

import github.easytcc.configuration.TccProperties;
import github.easytcc.factory.SpringBeanFactory;
import github.easytcc.repository.MetricsRepository;
import github.easytcc.repository.factory.RepositoryFactory;

/**
 * @author Fangfang.Xu
 *
 */
public class TimeWatcher {

	private long beginTimeMillis;

	private long usedMillis;

	TccProperties tccProperties;

	public TimeWatcher() {
		if (tccProperties == null) {
			try {
				tccProperties = SpringBeanFactory.getBean(TccProperties.class);
			} catch (Exception e) {
				// ignore
			}
		}
	}

	public void stageBegin() {
		beginTimeMillis = System.currentTimeMillis();
	}

	public void stageEnd() {
		if (beginTimeMillis != 0) {
			long currentTimeMillis = System.currentTimeMillis();
			usedMillis += currentTimeMillis - beginTimeMillis;
		}
	}

	public void end() {
		stageEnd();
		if (tccProperties != null && tccProperties.isMetricsEnabled()) {
			MetricsRepository metricsRepository = RepositoryFactory.getMetricsRepository();
			metricsRepository.xidMetric(usedMillis);
		}
	}
}
