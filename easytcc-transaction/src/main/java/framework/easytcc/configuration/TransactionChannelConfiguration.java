package framework.easytcc.configuration;

import org.apache.dubbo.remoting.RemotingException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import framework.easytcc.factory.ExtensionLoader;
import framework.easytcc.remoting.NonOpTransactionChannel;
import framework.easytcc.remoting.TransactionChannel;

/**
 * return NettyTransactionChannel if use netty module
	else return NonOpTransactionChannel
	or else return custom impl defined in classpath META-INF/services/framework.easytcc.remoting.TransactionChannel
	
	why define TransactionChannel bean in this module: 
	because TransactionChannel should not be null but can do nothing(un sync commit/rollback remote).
	if not define TransactionChannel here the bean of TransactionChannel will null when user not use remoting module
 * @author Fangfang.Xu
 *
 */
@Configuration
public class TransactionChannelConfiguration {

	@Bean
	public TransactionChannel transactionChannel() throws RemotingException {
		return ExtensionLoader.getExtension(TransactionChannel.class,new NonOpTransactionChannel());
	}
}
