package github.easytcc.metrics;

import github.easytcc.repository.MetricsRepository;
import github.easytcc.repository.factory.RepositoryFactory;

/**
 * @author Fangfang.Xu
 *
 */
public class TimeWatcher {

	private long beginTimeMillis;

	private long usedMillis;

	static MetricsRepository metricsRepository;

	public TimeWatcher() {
		if (metricsRepository == null) {
			try {
				metricsRepository = RepositoryFactory.getMetricsRepository();
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
		metricsRepository.xidDone(usedMillis);
	}
}
