package xff.bankout;

import org.junit.Test;

import xff.test.Exceptions;

/**
 * @author Fangfang.Xu
 *
 */
public class TransferOutTest {

	@Test
	public void testTransferOutNormal() throws Exception {
		ConcurrentTransfer.transferOut(new Exceptions(),100);
	}
	
	@Test
	public void testTransferOutExceptionBeforeBankinReturn() throws Exception {
		Exceptions exceptions = new Exceptions();
		exceptions.setExceptionBeforeBankinReturn(true);
		
		ConcurrentTransfer.transferOut(exceptions,100);
	}
	
	@Test
	public void testTransferOutExceptionBeforeBankoutScoreReturn() throws Exception {
		Exceptions exceptions = new Exceptions();
		exceptions.setExceptionBeforeBankoutScoreReturn(true);
		
		ConcurrentTransfer.transferOut(exceptions,100);
	}
	
	@Test
	public void testTransferOutExceptionBeforeBankinScoreReturn() throws Exception {
		Exceptions exceptions = new Exceptions();
		exceptions.setExceptionBeforeBankinScoreReturn(true);
		
		ConcurrentTransfer.transferOut(exceptions,100);
	}
	
	@Test
	public void testExceptionBankoutConfirm() throws Exception {
		Exceptions exceptions = new Exceptions();
		exceptions.setExceptionBankoutConfirm(true);
		
		ConcurrentTransfer.transferOut(exceptions,1);
	}
	@Test
	public void testExceptionBankoutCancel() throws Exception {
		Exceptions exceptions = new Exceptions();
		exceptions.setExceptionBeforeBankinReturn(true);
		exceptions.setExceptionBankoutCancel(true);
		
		ConcurrentTransfer.transferOut(exceptions,1);
	}
}
