package github.easytcc.remoting.netty;

import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.exchange.ExchangeChannel;
import org.apache.dubbo.remoting.exchange.support.Replier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import github.easytcc.remoting.ServerTransactionExecutor;
import github.easytcc.transaction.TransactionStatus;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class ServerTransactionReplier implements Replier<Message> {
	
	static Logger logger = LoggerFactory.getLogger(ServerTransactionReplier.class);
	
	final ServerTransactionExecutor serverTransactionExecutor;
	
	public ServerTransactionReplier(ServerTransactionExecutor serverTransactionExecutor) {
		this.serverTransactionExecutor = serverTransactionExecutor;
	}

    public Class<Message> interest() {
        return Message.class;
    }

    public Object reply(ExchangeChannel channel, Message message) throws RemotingException {
    	boolean result = true;
		try {
			if(logger.isDebugEnabled()) {
				logger.debug("recieved transaction message:{}",message);				
			}

			String xid = message.getXid();
			TransactionStatus transactionStatus = message.getTransactionStatus();
			
			serverTransactionExecutor.execute(xid, transactionStatus);
			
	        return result;
		} catch (Exception e) {
			result = false;
			logger.error("recieve and handle localTransaction failed",e);
		}
		return result;
    }

}