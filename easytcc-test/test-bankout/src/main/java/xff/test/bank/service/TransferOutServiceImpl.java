package xff.test.bank.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.easytcc.annotation.EasyTcc;
import xff.test.bank.entity.TransferOutAccount;
import xff.test.bank.entity.TransferOutOrder;
import xff.test.bank.repository.TransferOutAccountRepository;
import xff.test.bank.repository.TransferOutOrderRepository;
import xff.test.dto.ScoreIncrDto;
import xff.test.dto.TransferAmtDto;
/**
 * 
 * @author Fangfang.Xu
 */
@Service
public class TransferOutServiceImpl implements TransferOutService {

	@Autowired
	TransferOutOrderRepository transferOrderRepository;
	@Autowired
	TransferOutAccountRepository transferOutAccountRepository;
	@Autowired
	BankinServiceProxy bankinServiceProxy;
	@Autowired
	ScoreServiceProxy scoreServiceProxy;

	@EasyTcc(action = "Transfer", confirmMethod = "confirm", cancelMethod = "cancel")
	@Transactional
	@Override
	public TransferOutOrder transferAmt(TransferAmtDto transferOutDto) {
		System.out.println("start transfer out:" + transferOutDto.getTransferNumber());

		// create order
		TransferOutOrder transferOrder = new TransferOutOrder();
		transferOrder.setStatus("try");
		transferOrder.setTransferNumber(transferOutDto.getTransferNumber());
		transferOrder.setAmt(transferOutDto.getAmt());
		transferOrderRepository.save(transferOrder);
		
		//subtract amt
		TransferOutAccount transferOutAccount = transferOutAccountRepository.findAll().get(0);
		transferOutAccountRepository.sumAmt(transferOutAccount.getId(), -transferOutDto.getAmt());

		//bankin
		bankinServiceProxy.tranferToBankin(transferOutDto);

		//score
		ScoreIncrDto scoreIncrDto = new ScoreIncrDto();
		scoreIncrDto.setAccountId(transferOutAccount.getId());
		scoreIncrDto.setBankAccountType("out");
		scoreIncrDto.setIncrOrderNumber(UUID.randomUUID().toString());
		scoreIncrDto.setScore(10L);
		scoreIncrDto.setExceptions(transferOutDto.getExceptions());
		scoreServiceProxy.scoreincr(scoreIncrDto);
		
		return transferOrder;
	}

	@Transactional
	public void confirm(TransferAmtDto transferOutDto) {
		System.out.println("start confirm transfer out:" + transferOutDto.getTransferNumber());
		TransferOutOrder order = transferOrderRepository.findByTransferNumber(transferOutDto.getTransferNumber());
		if (order != null && "try".equals(order.getStatus())) {
			order.setStatus("confirm");
			transferOrderRepository.save(order);
		}
		System.out.println("confirm transfer success");
	}
	
	@Transactional
	public void cancel(TransferAmtDto transferOutDto) {
		System.out.println("start rollback transfer out:" + transferOutDto.getTransferNumber());
		TransferOutOrder order = transferOrderRepository.findByTransferNumber(transferOutDto.getTransferNumber());
		if (order != null && "try".equals(order.getStatus())) {
			//order cancel
			order.setStatus("cancel");
			transferOrderRepository.save(order);
			
			TransferOutAccount transferOutAccount = transferOutAccountRepository.findAll().get(0);
			//recovery sum acct amt
			transferOutAccountRepository.sumAmt(transferOutAccount.getId(), transferOutDto.getAmt());
		}

		System.out.println("rollback transfer success");
	}

}
