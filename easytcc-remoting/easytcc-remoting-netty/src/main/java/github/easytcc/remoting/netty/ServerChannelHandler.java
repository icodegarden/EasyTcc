package github.easytcc.remoting.netty;

import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.RemotingException;

import github.easytcc.remoting.netty.repository.NettyRepository;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class ServerChannelHandler implements ChannelHandler {
	
	private NettyRepository nettyRepository;
	private String address;
	private int port;
	
	public ServerChannelHandler(NettyRepository nettyRepository,String address,int port) {
		this.nettyRepository = nettyRepository;
		this.address = address;
		this.port = port;
	}

	@Override
	public void connected(Channel channel) throws RemotingException {
		nettyRepository.incrConnection(address, port);
	}

	@Override
	public void disconnected(Channel channel) throws RemotingException {
		nettyRepository.decrConnection(address, port);
	}

	@Override
	public void sent(Channel channel, Object message) throws RemotingException {
	}

	@Override
	public void received(Channel channel, Object message) throws RemotingException {
	}

	@Override
	public void caught(Channel channel, Throwable exception) throws RemotingException {
	}

}
