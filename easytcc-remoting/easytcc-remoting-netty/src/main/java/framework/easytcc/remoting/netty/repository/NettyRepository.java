package framework.easytcc.remoting.netty.repository;

/**
 * @author Fangfang.Xu
 *
 */
public interface NettyRepository {

	void addServer(String address,int port,long expireMills);
	
	void updateExpire(String address,int port,long expireMills);
	
	void incrConnection(String address,int port);
	
	void decrConnection(String address,int port);
	
	String getSuitableServer(String applicationName);
	
	void removeServer(String applicationName,String address,int port);
}
