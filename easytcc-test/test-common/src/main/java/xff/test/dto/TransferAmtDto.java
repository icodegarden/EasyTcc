package xff.test.dto;

import java.io.Serializable;

import xff.test.Exceptions;

public class TransferAmtDto implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Exceptions exceptions = new Exceptions();

	private String transferNumber;
	
	private Long amt;

	public Exceptions getExceptions() {
		return exceptions;
	}

	public void setExceptions(Exceptions exceptions) {
		this.exceptions = exceptions;
	}

	public String getTransferNumber() {
		return transferNumber;
	}

	public void setTransferNumber(String transferNumber) {
		this.transferNumber = transferNumber;
	}

	public Long getAmt() {
		return amt;
	}

	public void setAmt(Long amt) {
		this.amt = amt;
	}
}
