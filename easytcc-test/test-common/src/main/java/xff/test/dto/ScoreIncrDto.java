package xff.test.dto;

import java.io.Serializable;

import xff.test.Exceptions;

public class ScoreIncrDto implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Exceptions exceptions = new Exceptions();

	private Long accountId;

	private String bankAccountType;//out in
	
	private String incrOrderNumber;
	
	private Long score;
	
	public ScoreIncrDto() {
		
	}
	
	public Exceptions getExceptions() {
		return exceptions;
	}

	public void setExceptions(Exceptions exceptions) {
		this.exceptions = exceptions;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getBankAccountType() {
		return bankAccountType;
	}

	public void setBankAccountType(String bankAccountType) {
		this.bankAccountType = bankAccountType;
	}

	public String getIncrOrderNumber() {
		return incrOrderNumber;
	}

	public void setIncrOrderNumber(String incrOrderNumber) {
		this.incrOrderNumber = incrOrderNumber;
	}

	public Long getScore() {
		return score;
	}

	public void setScore(Long score) {
		this.score = score;
	}

}
