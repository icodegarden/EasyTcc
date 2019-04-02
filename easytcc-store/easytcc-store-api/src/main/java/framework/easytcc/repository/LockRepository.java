package framework.easytcc.repository;

/**
 * @author Fangfang.Xu
 *
 */
public interface LockRepository {
	/**
	 * block acquire lock until success or timeout
	 * @param lockName 
	 * @param acquireTimeoutMillis	acquire timeout,if 0 will not timeout
	 * @param lockTimeoutMillis	lock inuse timeout
	 */
	LockResult acquireLockBlock(String lockName,final long acquireTimeoutMillis,final long lockTimeoutMillis);
	/**
	 * NonBlock acquire lock
	 */
	LockResult acquireLockNonBlock(String lockName,final long lockTimeoutMillis);
	
	void releaseLock(LockResult result);
	/**
	 * lock result
	 */
	class LockResult{
		private String lockName;
		private boolean acquire;
		private String identifier;
		public LockResult(String lockName,boolean acquire, String identifier) {
			this.lockName = lockName;
			this.acquire = acquire;
			this.identifier = identifier;
		}
		public String getLockName() {
			return lockName;
		}
		/**
		 *	success or fail
		 */
		public boolean isAcquire() {
			return acquire;
		}
		public String getIdentifier() {
			return identifier;
		}
		@Override
		public String toString() {
			return "LockResult [lockName=" + lockName + ", acquire=" + acquire + ", identifier=" + identifier + "]";
		}
	}
}
