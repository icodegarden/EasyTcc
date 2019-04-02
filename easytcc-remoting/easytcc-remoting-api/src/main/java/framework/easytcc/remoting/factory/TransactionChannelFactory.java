package framework.easytcc.remoting.factory;

import framework.easytcc.factory.SpringBeanFactory;
import framework.easytcc.remoting.NonOpTransactionChannel;
import framework.easytcc.remoting.TransactionChannel;

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
				//non oper TransactionChannel
				transactionChannel = new NonOpTransactionChannel();
			}
		}
		return transactionChannel;
	}
}
