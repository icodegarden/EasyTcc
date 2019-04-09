package github.easytcc.metrics;

/**
 * @author Fangfang.Xu
 *
 */
public class TimeWatcher {

	private long beginTimeMillis;

	private long usedMillis;

	public void stageBegin() {
		beginTimeMillis = System.currentTimeMillis();
	}

	public void stageEnd() {
		if (beginTimeMillis != 0) {
			long currentTimeMillis = System.currentTimeMillis();
			usedMillis += currentTimeMillis - beginTimeMillis;
		}
	}
	
	public long getUsedMillis() {
		return usedMillis;
	}

	public void end() {
		stageEnd();
	}
}
