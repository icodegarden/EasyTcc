package github.easytcc.remoting.netty.repository;

/**
 * @author Fangfang.Xu
 *
 */
public interface NettyRepository {

	/**
	 * 
	 * @param address
	 * @param port
	 * @param weight	for client loadbalance
	 * @param expireMills
	 */
	void addServer(String address,int port,int weight,long expireMills);
	
	void updateExpire(String address,int port,long expireMills);
	
	void incrConnection(String address,int port);
	
	void decrConnection(String address,int port);
	
	String getSuitableServer(String applicationName);
	
	void removeServer(String applicationName,String address,int port);
}
