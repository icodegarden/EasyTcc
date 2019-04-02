package framework.easytcc.repository.redis;


import java.util.Date;

import framework.easytcc.configuration.TccProperties;
import framework.easytcc.repository.MetricsRepository;
import framework.easytcc.util.DateFormatUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * @author Fangfang.Xu
 *
 */
public class RedisMetricsRepository extends AbstractRedisRepository implements MetricsRepository{
	
	private static final String KEY_XID_COUNT = "xid-count";
	private static final String KEY_XID_COUNT_DAY_PREFIX = "xid-count-day:";
	private static final String KEY_XID_COUNT_USED_MILLIS = "xid-count-used-millis";
	private static final String KEY_XID_COUNT_USED_MILLIS_DAY_PREFIX = "xid-count-used-millis-day:";
	
	public RedisMetricsRepository(RedisResource redisResource,TccProperties tccProperties) {
		super(redisResource,tccProperties);
	}
	
	@Override
	public void xidMetric(long usedMillis) {
		Jedis jedis = null;
		Pipeline pipeline = null;
		try{
			jedis = redisResource.getResource();
			pipeline = jedis.pipelined();
			pipeline.multi();
			
			String ymd = DateFormatUtils.ymdFormat(new Date());
			
			pipeline.incr(KEY_XID_COUNT);
			pipeline.incr(KEY_XID_COUNT_DAY_PREFIX + ymd);
			pipeline.incrBy(KEY_XID_COUNT_USED_MILLIS,usedMillis);
			pipeline.incrBy(KEY_XID_COUNT_USED_MILLIS_DAY_PREFIX + ymd,usedMillis);
			
			pipeline.exec();
		}finally {
			redisResource.close(pipeline);
			redisResource.close(jedis);
		}
	}
}
