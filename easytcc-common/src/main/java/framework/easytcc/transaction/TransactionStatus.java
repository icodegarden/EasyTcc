package framework.easytcc.transaction;

/**
 * @author Fangfang.Xu
 *
 */
public enum TransactionStatus {

	BEGIN("0"),COMMIT("1"),ROLLBACK("-1");
	
	private String status;
	
	private TransactionStatus(String status){
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
	
	public static TransactionStatus get(String status){
		TransactionStatus[] transactionStatuses = TransactionStatus.values();
		for(TransactionStatus transactionStatus:transactionStatuses){
			if(transactionStatus.getStatus().equals(status)){
				return transactionStatus;
			}
		}
		throw new IllegalArgumentException("no TransactionStatus for "+status);
	}
}
