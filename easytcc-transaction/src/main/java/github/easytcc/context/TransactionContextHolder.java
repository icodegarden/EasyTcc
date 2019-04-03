package github.easytcc.context;

/**
 * @author Fangfang.Xu
 *
 */
public class TransactionContextHolder {
	
	private static ThreadLocal<TransactionContext> local = new ThreadLocal<TransactionContext>();

	public static TransactionContext getContext(){
		return local.get();
	}
	
	public static void setContext(TransactionContext transactionContext){
		local.set(transactionContext);
	}
	
	public static void clearContext(){
		local.remove();
	}
}
