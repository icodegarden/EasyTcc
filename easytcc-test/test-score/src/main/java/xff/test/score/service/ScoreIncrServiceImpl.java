package xff.test.score.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.easytcc.annotation.EasyTcc;
import xff.test.dto.ScoreIncrDto;
import xff.test.score.entity.ScoreAccount;
import xff.test.score.entity.ScoreIncrOrder;
import xff.test.score.repository.ScoreAccountRepository;
import xff.test.score.repository.ScoreIncrOrderRepository;

/**
 * @author Fangfang.Xu
 *
 */
@Service
public class ScoreIncrServiceImpl implements ScoreIncrService {

	@Autowired
	ScoreIncrOrderRepository scoreIncrOrderRepository;
	@Autowired
	ScoreAccountRepository scoreAccountRepository;

	@EasyTcc(confirmMethod = "confirm", cancelMethod = "cancel")
	@Transactional
	@Override
	public ScoreIncrOrder scoreIncr(ScoreIncrDto scoreIncrDto) {
		System.out.println("start score in, accountId:" + scoreIncrDto.getAccountId());

		// create order
		ScoreIncrOrder order = new ScoreIncrOrder();
		order.setBankAccountType(scoreIncrDto.getBankAccountType());
		order.setAccountId(scoreIncrDto.getAccountId());
		order.setIncrOrderNumber(scoreIncrDto.getIncrOrderNumber());
		order.setScore(scoreIncrDto.getScore());
		order.setStatus("try");
		ScoreIncrOrder save = scoreIncrOrderRepository.save(order);
		
		//for test
		if(scoreIncrDto.getBankAccountType().equals("out")) {
			scoreIncrDto.getExceptions().exceptionBeforeBankoutScoreReturn();			
		}
		if(scoreIncrDto.getBankAccountType().equals("in")) {
			scoreIncrDto.getExceptions().exceptionBeforeBankinScoreReturn();			
		}
		return save;
	}

	@Transactional
	public void confirm(ScoreIncrDto scoreIncrDto) {
		System.out.println("start confirm score incr bankAccountType:" + scoreIncrDto.getBankAccountType());

		ScoreIncrOrder order = scoreIncrOrderRepository.findByIncrOrderNumber(scoreIncrDto.getIncrOrderNumber());
		if (order != null && order.getStatus().equals("try")) {
			ScoreAccount scoreAccount = null;
			if (scoreIncrDto.getBankAccountType().equals("out")) {
				scoreAccount = scoreAccountRepository.findByBankOutAccountId(scoreIncrDto.getAccountId());
			} else if (scoreIncrDto.getBankAccountType().equals("in")) {
				scoreAccount = scoreAccountRepository.findByBankInAccountId(scoreIncrDto.getAccountId());
			} else {
				throw new RuntimeException("no bankAccountType for " + scoreIncrDto.getBankAccountType());
			}

			order.setStatus("confirm");
			scoreIncrOrderRepository.save(order);

			// sum score
			scoreAccountRepository.sumScore(scoreAccount.getId(), scoreIncrDto.getScore());
		}

		System.out.println("confirm score incr success");
	}

	@Transactional
	public void cancel(ScoreIncrDto scoreIncrDto) {
		System.out.println("start rollback score incr bankAccountType:" + scoreIncrDto.getBankAccountType());

		ScoreIncrOrder order = scoreIncrOrderRepository.findByIncrOrderNumber(scoreIncrDto.getIncrOrderNumber());
		if (order != null && order.getStatus().equals("try")) {
			order.setStatus("cancel");
			scoreIncrOrderRepository.save(order);
		}

		System.out.println("rollback score incr success");
	}

}
