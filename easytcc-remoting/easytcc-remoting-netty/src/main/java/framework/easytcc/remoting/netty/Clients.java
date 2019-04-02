package framework.easytcc.remoting.netty;

import java.util.HashMap;
import java.util.Map;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.exchange.ExchangeClient;
import org.apache.dubbo.remoting.exchange.Exchangers;

import framework.easytcc.exception.TccException;
import framework.easytcc.remoting.netty.configuration.NettyProperties;
import framework.easytcc.remoting.netty.repository.NettyRepository;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class Clients {

	NettyRepository nettyRepository;

	private volatile Map<String, ExchangeClient> cachedClients = new HashMap<String, ExchangeClient>();

	NettyProperties nettyProperties;

	public Clients(NettyRepository nettyRepository, NettyProperties nettyProperties) {
		this.nettyRepository = nettyRepository;
		this.nettyProperties = nettyProperties;
	}

	public ExchangeClient getClient(String application) {
		ExchangeClient client = cachedClients.get(application);
		if (client != null && client.isClosed()) {
			client.close();
			cachedClients.remove(application);
			client = null;
		}
		if (client == null) {
			synchronized (Clients.class) {
				client = cachedClients.get(application);
				if (client == null) {
					String hostport = nettyRepository.getSuitableServer(application);
					if (hostport == null) {
						throw new TccException("can not found suitable netty server for application : " + application);
					}
					try {
						client = Exchangers.connect(URL.valueOf("exchange://" + hostport + "?client=netty4&heartbeat="
								+ nettyProperties.getHeartbeat()));
						cachedClients.put(application, client);
					} catch (RemotingException e) {
						throw new TccException(e);
					}
				}
			}
		}
		return client;
	}
}
