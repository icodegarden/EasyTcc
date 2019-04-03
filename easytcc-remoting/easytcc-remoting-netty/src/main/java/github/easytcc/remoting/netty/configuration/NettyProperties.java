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

	@Value("${" + PREFIX + ".port:16789}")
	private int nettyServerPort;
	
	@Value("${" + PREFIX + ".threadpool:cached}")
	private String threadpool;
	
	@Value("${" + PREFIX + ".requestTimeout:3000}")
	private int requestTimeout;
	
	@Value("${" + PREFIX + ".responseTimeout:3000}")
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
}
