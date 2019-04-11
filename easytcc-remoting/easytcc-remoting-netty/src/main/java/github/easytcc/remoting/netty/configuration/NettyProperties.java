package github.easytcc.remoting.netty.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import github.easytcc.configuration.TccProperties;

/**
 * @author Fangfang.Xu
 *
 */
@Configuration
public class NettyProperties {

	static Logger logger = LoggerFactory.getLogger(NettyProperties.class);

	static final String PREFIX = "easytcc.netty";

	@Value("${" + PREFIX + ".server.port:16789}")
	private int nettyServerPort;
	
	//1-100
	@Value("${" + PREFIX + ".server.weight:50}")
	private int nettyServerWeight;
	
	@Value("${" + PREFIX + ".server.threadpool:cached}")
	private String threadpool;
	
	@Value("${" + PREFIX + ".server.threadname:EasyTccNettyServer}")
	private String threadname;
	
	@Value("${" + PREFIX + ".server.corethreads:30}")
	private int corethreads;
	
	@Value("${" + PREFIX + ".server.threads:60}")
	private int threads;
	
	@Value("${" + PREFIX + ".server.queues:"+Integer.MAX_VALUE+"}")
	private int queues;
	
	@Value("${" + PREFIX + ".server.aliveMillis:60000}")
	private int aliveMillis;
	
	@Value("${" + PREFIX + ".client.requestTimeout:3000}")
	private int requestTimeout;
	
	@Value("${" + PREFIX + ".client.responseTimeout:3000}")
	private int responseTimeout;
	
	@Value("${" + PREFIX + ".heartbeat:10000}")
	private int heartbeat;
	
	@Value("${" + PREFIX + ".repository.expireMillis:20000}")
	private int repositoryExpireMills;
	
	@Value("${" + PREFIX + ".repository.updateExpireRateMillis:5000}")
	private int repositoryUpdateExpireRateMillis;
	
	@Autowired
	private TccProperties tccProperties; 
	
	public TccProperties getTccProperties() {
		return tccProperties;
	}
	
	public int getNettyServerPort() {
		return nettyServerPort;
	}

	public int getRequestTimeout() {
		return requestTimeout;
	}

	public int getResponseTimeout() {
		return responseTimeout;
	}

	public int getHeartbeat() {
		return heartbeat;
	}

	public int getRepositoryExpireMills() {
		return repositoryExpireMills;
	}

	public int getRepositoryUpdateExpireRateMillis() {
		return repositoryUpdateExpireRateMillis;
	}
	
	public String getThreadpool() {
		return threadpool;
	}

	public int getNettyServerWeight() {
		return nettyServerWeight;
	}

	public String getThreadname() {
		return threadname;
	}

	public int getCorethreads() {
		return corethreads;
	}

	public int getThreads() {
		return threads;
	}

	public int getQueues() {
		return queues;
	}

	public int getAliveMillis() {
		return aliveMillis;
	}
	
}
