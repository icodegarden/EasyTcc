package github.easytcc;

import github.easytcc.metrics.TimeWatcher;
import github.easytcc.repository.MetricsRepository;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class MetricsInterceptor implements TransactionAspectInterceptor {

	static final String META_TIMEWATCHER = "timeWatcher";
	
	final MetricsRepository metricsRepository;
	
	public MetricsInterceptor(MetricsRepository metricsRepository) {
		this.metricsRepository = metricsRepository;
	}

	@Override
	public int getOrder() {
		return HIGHEST_PRECEDENCE;
	}

	@Override
	public void preHandle(ProceedingJoinPointWrapper pointWrapper, ExecutionChain chain) throws Exception {
		TimeWatcher timeWatcher = new TimeWatcher();

		if (pointWrapper.isXidCreateStack()) {
			timeWatcher.stageBegin();
		}

		chain.preHandle(pointWrapper, chain);

		if (pointWrapper.isXidCreateStack()) {
			timeWatcher.stageEnd();
		}

		pointWrapper.putMetadata(META_TIMEWATCHER, timeWatcher);
	}

	@Override
	public void postHandle(ProceedingJoinPointWrapper pointWrapper) {
		TimeWatcher timeWatcher = (TimeWatcher) pointWrapper.getMetadata(META_TIMEWATCHER);
		if (pointWrapper.isXidCreateStack()) {
			timeWatcher.stageBegin();
		}
	}

	@Override
	public void afterCompletion(ProceedingJoinPointWrapper pointWrapper, Throwable e) {
		TimeWatcher timeWatcher = (TimeWatcher) pointWrapper.getMetadata(META_TIMEWATCHER);
		if (pointWrapper.isXidCreateStack()) {
			try {
				timeWatcher.end();
				metricsRepository.xidDone(timeWatcher.getUsedMillis());
			} catch (Exception e1) {
				// ignore
			}
		}
	}
}
