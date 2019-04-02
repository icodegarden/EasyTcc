package xff.test;

import java.io.Serializable;

/**
 * @author Fangfang.Xu
 *
 */
public class Exceptions implements Serializable{

	boolean exceptionBeforeBankinReturn = false;
	
	boolean exceptionBeforeBankoutScoreReturn = false;
	
	boolean exceptionBeforeBankinScoreReturn = false;
	
	public void exceptionBeforeBankinReturn() {
		if(exceptionBeforeBankinReturn) {
			throw new RuntimeException("exceptionBeforeBankinReturn");
		}
	}
	
	public void exceptionBeforeBankoutScoreReturn() {
		if(exceptionBeforeBankoutScoreReturn) {
			throw new RuntimeException("exceptionBeforeBankoutScoreReturn");
		}
	}
	
	public void exceptionBeforeBankinScoreReturn() {
		if(exceptionBeforeBankinScoreReturn) {
			throw new RuntimeException("exceptionBeforeBankinScoreReturn");
		}
	}

	public void setExceptionBeforeBankinReturn(boolean exceptionBeforeBankinReturn) {
		this.exceptionBeforeBankinReturn = exceptionBeforeBankinReturn;
	}

	public void setExceptionBeforeBankoutScoreReturn(boolean exceptionBeforeBankoutScoreReturn) {
		this.exceptionBeforeBankoutScoreReturn = exceptionBeforeBankoutScoreReturn;
	}

	public void setExceptionBeforeBankinScoreReturn(boolean exceptionBeforeBankinScoreReturn) {
		this.exceptionBeforeBankinScoreReturn = exceptionBeforeBankinScoreReturn;
	}

	public boolean isExceptionBeforeBankinReturn() {
		return exceptionBeforeBankinReturn;
	}

	public boolean isExceptionBeforeBankoutScoreReturn() {
		return exceptionBeforeBankoutScoreReturn;
	}

	public boolean isExceptionBeforeBankinScoreReturn() {
		return exceptionBeforeBankinScoreReturn;
	}

	
}
