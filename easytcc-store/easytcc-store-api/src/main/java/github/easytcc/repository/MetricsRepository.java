package github.easytcc.repository;

/**
 * @author Fangfang.Xu
 *
 */
public interface MetricsRepository {

	void xidDone(long usedMillis);
	
	class NoOpMetricsRepository implements MetricsRepository{
		@Override
		public void xidDone(long usedMillis) {
		}
	}
}
