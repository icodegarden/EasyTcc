package framework.easytcc.transaction;

import java.io.Serializable;

/**
 * @author Fangfang.Xu
 *
 */
public interface Transaction extends Serializable{

	String getXid();
	
	String getTransactionId();
	
	boolean commit();
	
	boolean rollback();
	
}
