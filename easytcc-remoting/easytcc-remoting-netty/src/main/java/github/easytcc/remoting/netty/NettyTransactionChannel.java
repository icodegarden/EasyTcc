package github.easytcc.remoting.netty;

import javax.annotation.PostConstruct;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.exchange.ExchangeClient;
import org.apache.dubbo.remoting.exchange.ExchangeServer;
import org.apache.dubbo.remoting.exchange.Exchangers;
import org.apache.dubbo.remoting.exchange.ResponseFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStoppedEvent;

import github.easytcc.configuration.TccProperties;
import github.easytcc.exception.TccException;
import github.easytcc.remoting.TransactionChannel;
import github.easytcc.transaction.TransactionStatus;
import github.easytcc.remoting.netty.configuration.NettyProperties;
import github.easytcc.remoting.netty.repository.NettyRepository;

/**
 * @author Fangfang.Xu
 *
 */
public class NettyTransactionChannel implements TransactionChannel, ApplicationListener<ApplicationEvent> {

	static Logger logger = LoggerFactory.getLogger(NettyTransactionChannel.class);

	String bind = NetUtils.getLocalHost();

	@Autowired
	NettyRepository nettyRepository;
	@Autowired
	TccProperties tccProperties;
	@Autowired
	NettyProperties nettyProperties;

	ExchangeServer server;

	Clients clients;

	@PostConstruct
	public void init() throws RemotingException {
		server = Exchangers.bind(
				URL.valueOf(
						"exchange://" + bind + ":" + nettyProperties.getNettyServerPort() + "?server=netty4&heartbeat="
								+ nettyProperties.getHeartbeat() + "&threadpool=" + nettyProperties.getThreadpool()),
				new ServerChannelHandler(nettyRepository, bind, nettyProperties.getNettyServerPort()),
				new ServerTransactionReplier());

		// add to repo
		try {
			nettyRepository.addServer(bind, nettyProperties.getNettyServerPort(),
					nettyProperties.getNettyServerWeight(), nettyProperties.getRepositoryExpireMills());
		} catch (Exception e) {
			server.close();
			server = null;
			throw new TccException(e);
		}
		new Thread(new Runnable() {
			public void run() {
				while (!server.isClosed()) {
					try {
						Thread.sleep(nettyProperties.getRepositoryUpdateExpireRateMillis());
					} catch (InterruptedException e) {
					}
					try {
						nettyRepository.updateExpire(bind, nettyProperties.getNettyServerPort(),
								nettyProperties.getRepositoryExpireMills());
					} catch (Exception e) {
						// ignore
					}
				}
			};
		}, "nettyServerExpireUpdater(" + server + ")").start();
		clients = new Clients(nettyRepository, nettyProperties);
	}

	@Override
	public void preDownstreamAnnotedMethodExec(String upstreamApplication) {
	}

	@Override
	public boolean sendTransaction(String application, String xid, TransactionStatus transactionStatus) {
		ExchangeClient client = clients.getClient(application);
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("netty prepare sendTransaction,application:{},transactionStatus:{}", application,
						transactionStatus);
			}
			Message message = new Message(xid, transactionStatus);
			ResponseFuture responseFuture = client.request(message, nettyProperties.getRequestTimeout());
			return (Boolean) responseFuture.get(nettyProperties.getResponseTimeout());
		} catch (RemotingException e) {
			logger.error("netty sendTransaction failed", e);
			return false;
		}
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextClosedEvent || event instanceof ContextStoppedEvent) {
			if (server != null && tccProperties != null) {
				nettyRepository.removeServer(tccProperties.getApplication(), bind,
						nettyProperties.getNettyServerPort());
			}
		}
	}

}