package github.easytcc.remoting.netty;

import java.util.HashMap;
import java.util.Map;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.exchange.ExchangeClient;
import org.apache.dubbo.remoting.exchange.Exchangers;

import github.easytcc.exception.TccException;
import github.easytcc.remoting.netty.configuration.NettyProperties;
import github.easytcc.remoting.netty.repository.NettyRepository;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class Clients {

	NettyRepository nettyRepository;

	private volatile Map<String, Holder<ExchangeClient>> cachedClients = new HashMap<String, Holder<ExchangeClient>>();

	NettyProperties nettyProperties;

	public Clients(NettyRepository nettyRepository, NettyProperties nettyProperties) {
		this.nettyRepository = nettyRepository;
		this.nettyProperties = nettyProperties;
	}

	public ExchangeClient getClient(String application) {
		Holder<ExchangeClient> holder = cachedClients.get(application);
		if (holder == null) {
			cachedClients.putIfAbsent(application, new Holder<ExchangeClient>());
			holder = cachedClients.get(application);
		}
		ExchangeClient client = holder.get();
		if (client != null && client.isClosed()) {
			client.close();
			holder.remove();
			client = null;
		}
		if (client == null) {
			synchronized (holder) {
				client = holder.get();
				if (client == null) {
					String hostport = nettyRepository.getSuitableServer(application);
					if (hostport == null) {
						throw new TccException("can not found suitable netty server for application : " + application);
					}
					try {
						client = Exchangers.connect(URL.valueOf("exchange://" + hostport + "?client=netty4&heartbeat="
								+ nettyProperties.getHeartbeat()));
						holder.set(client);
					} catch (RemotingException e) {
						throw new TccException(e);
					}
				}
			}
		}
		return client;
	}

	class Holder<T> {

		private volatile T value;

		public void set(T value) {
			this.value = value;
		}

		public T get() {
			return value;
		}

		public void remove() {
			this.value = null;
		}
	}
}
