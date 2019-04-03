package github.easytcc.repository.redis;

import java.util.UUID;

import github.easytcc.configuration.TccProperties;
import github.easytcc.repository.LockRepository;
import redis.clients.jedis.Jedis;

/**
 * @author Fangfang.Xu
 *
 */
public class RedisLockRepository extends AbstractRedisRepository implements LockRepository {

	public RedisLockRepository(RedisResource redisResource, TccProperties tccProperties) {
		super(redisResource, tccProperties);
	}

	@Override
	public LockResult acquireLockBlock(String lockName, long acquireTimeoutMillis, long lockTimeoutMillis) {
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();
			return RedisLock.acquireBlock(jedis, lockName, acquireTimeoutMillis, lockTimeoutMillis, 100);
		} finally {
			close(jedis);
		}
	}

	@Override
	public LockResult acquireLockNonBlock(String lockName, long lockTimeoutMillis) {
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();
			return RedisLock.acquireNonBlock(jedis, lockName, lockTimeoutMillis);
		} finally {
			close(jedis);
		}
	}

	@Override
	public void releaseLock(LockResult result) {
		Jedis jedis = null;
		try {
			jedis = redisResource.getResource();
			RedisLock.release(jedis, result);
		} finally {
			close(jedis);
		}
	}

	/**
	 * 
	 * @author Fangfang.Xu
	 *
	 */
	static class RedisLock {

		private static final String NX = "NX";
		private static final String EX = "EX";// seconds
		private static final String PX = "PX";// millisSeconds

		/**
		 * block acquire lock until success or timeout
		 * 
		 * @param acquireTimeoutMillis acquire timeout,if 0 will not timeout
		 * @param lockTimeoutMillis    lock inuse timeout
		 */
		public static LockResult acquireBlock(Jedis jedis, String lockName, final long acquireTimeoutMillis,
				final long lockTimeoutMillis, final long acquireRateMillis) {
			long end = System.currentTimeMillis() + acquireTimeoutMillis;
			while (acquireTimeoutMillis == 0 || System.currentTimeMillis() < end) {
				String uuid = UUID();
				String locked = tryLock(jedis, lockName, uuid, lockTimeoutMillis);
				if (locked != null) {
					return new LockResult(lockName, true, uuid);
				}
				try {
					Thread.sleep(acquireRateMillis);
				} catch (InterruptedException e) {
				}
			}
			return new LockResult(lockName, false, null);
		}

		public static LockResult acquireNonBlock(Jedis jedis, String lockName, final long lockTimeoutMillis) {
			String uuid = UUID();
			String locked = tryLock(jedis, lockName, uuid, lockTimeoutMillis);
			if (locked == null) {
				return new LockResult(lockName, false, null);
			}
			return new LockResult(lockName, true, uuid);
		}

		private static String tryLock(Jedis jedis, String lockName, String value, final long lockTimeoutMillis) {
			return jedis.set(lockName, value, NX, PX, lockTimeoutMillis);
		}

		private static String UUID() {
			return UUID.randomUUID().toString();
		}

		public static void release(Jedis jedis, LockResult result) {
			if (result.isAcquire()) {
				String lockName = result.getLockName();
				if (result.getIdentifier().equals(jedis.get(lockName))) {
					jedis.del(lockName);
				}
			}
		}
	}
}
