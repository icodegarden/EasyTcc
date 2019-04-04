package github.easytcc.remoting.factory;

import github.easytcc.factory.SpringBeanFactory;
import github.easytcc.remoting.NoOpTransactionChannel;
import github.easytcc.remoting.TransactionChannel;

/**
 * @author Fangfang.Xu
 */
public class TransactionChannelFactory {

	private static TransactionChannel transactionChannel;
	
	private static Throwable getBeanEx;
	
	public static TransactionChannel getTransactionChannel(){
		if(getBeanEx == null && transactionChannel == null) {
			try{
				transactionChannel = SpringBeanFactory.getBean(TransactionChannel.class);			
			}catch (Exception e) {
				getBeanEx = e;
				//no oper TransactionChannel
				transactionChannel = new NoOpTransactionChannel();
			}
		}
		return transactionChannel;
	}
}
