package xff.test.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import xff.test.bank.entity.TransferInAccount;
import xff.test.bank.entity.TransferInOrder;
import xff.test.bank.repository.TransferInAccountRepository;
import xff.test.bank.service.TransferInServiceImpl;
import xff.test.dto.TransferAmtDto;

/**
 * @author Fangfang.Xu
 *
 */
@RestController
public class TransferInController {
	
	@Autowired
	TransferInServiceImpl transferInService;
	@Autowired
	TransferInAccountRepository transferInAccountRepository;
	
	@RequestMapping(value="/transferin",method=RequestMethod.POST,produces="application/json")
	public TransferInOrder transferin(@RequestBody TransferAmtDto transferInDto){
		TransferInOrder transferAmt = transferInService.transferAmt(transferInDto);
		return transferAmt;
	}
	
	@RequestMapping(value="/transferInAccount",method=RequestMethod.GET,produces="application/json")
	public TransferInAccount transferInAccount(){
		TransferInAccount transferInAccount = transferInAccountRepository.findAll().get(0);
		return transferInAccount;
	}
}
