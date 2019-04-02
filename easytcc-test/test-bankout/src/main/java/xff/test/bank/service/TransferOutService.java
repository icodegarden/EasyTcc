package xff.test.bank.service;

import xff.test.bank.entity.TransferOutOrder;
import xff.test.dto.TransferAmtDto;
/**
 * 
 * @author Fangfang.Xu
 */
public interface TransferOutService {

	TransferOutOrder transferAmt(TransferAmtDto transferOutDto);
}
