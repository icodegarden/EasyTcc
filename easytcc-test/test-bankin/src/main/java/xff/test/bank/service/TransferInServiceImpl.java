package xff.test.bank.service;


import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.easytcc.annotation.EasyTcc;
import xff.test.bank.entity.TransferInAccount;
import xff.test.bank.entity.TransferInOrder;
import xff.test.bank.repository.TransferInAccountRepository;
import xff.test.bank.repository.TransferInOrderRepository;
import xff.test.dto.ScoreIncrDto;
import xff.test.dto.TransferAmtDto;
/**
 * 
 * @author Fangfang.Xu
 */
@Service
public class TransferInServiceImpl {
	
	@Autowired
	TransferInOrderRepository transferOrderRepository;
	@Autowired
	TransferInAccountRepository transferInAccountRepository;
	@Autowired
	ScoreServiceProxy scoreServiceProxy;
	
	@EasyTcc(confirmMethod="confirm",cancelMethod="cancel")
	@Transactional
	public TransferInOrder transferAmt(TransferAmtDto transferInDto) {
		System.out.println("start transfer in:"+transferInDto.getTransferNumber());
		
		TransferInAccount transferInAccount = transferInAccountRepository.findAll().get(0);
		
		//for test , call score before local
		ScoreIncrDto scoreIncrDto = new ScoreIncrDto();
		scoreIncrDto.setAccountId(transferInAccount.getId());
		scoreIncrDto.setBankAccountType("in");
		scoreIncrDto.setIncrOrderNumber(UUID.randomUUID().toString());
		scoreIncrDto.setScore(10L);
		scoreIncrDto.setExceptions(transferInDto.getExceptions());
		scoreServiceProxy.scoreincr(scoreIncrDto);
		
		//create order
		TransferInOrder transferOrder = new TransferInOrder();
		transferOrder.setStatus("try");
		transferOrder.setTransferNumber(transferInDto.getTransferNumber());
		transferOrder.setAmt(transferInDto.getAmt());
		transferOrderRepository.save(transferOrder);
		
		//for test
		transferInDto.getExceptions().exceptionBeforeBankinReturn();
		
		return transferOrder;
	}

	@Transactional
	public void confirm(TransferAmtDto transferInDto) {
		System.out.println("start confirm transfer in:"+transferInDto.getTransferNumber());
		
		TransferInOrder order = transferOrderRepository.findByTransferNumber(transferInDto.getTransferNumber());
		if(order != null && "try".equals(order.getStatus())){
			order.setStatus("confirm");
			transferOrderRepository.save(order);

			//sum amt
			TransferInAccount transferInAccount = transferInAccountRepository.findAll().get(0);
			transferInAccountRepository.sumAmt(transferInAccount.getId(), transferInDto.getAmt());
		}
		System.out.println("confirm transfer success");
	}
	
	@Transactional
	public void cancel(TransferAmtDto transferInDto) {
		System.out.println("start rollback transfer out:"+transferInDto.getTransferNumber());
		TransferInOrder order = transferOrderRepository.findByTransferNumber(transferInDto.getTransferNumber());
		if(order != null && "try".equals(order.getStatus())){
			order.setStatus("cancel");
			transferOrderRepository.save(order);
		}
		System.out.println("rollback transfer success");
	}
}
