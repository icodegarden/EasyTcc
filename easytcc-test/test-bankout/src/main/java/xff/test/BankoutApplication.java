package xff.test;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import xff.test.bank.entity.TransferOutAccount;
import xff.test.bank.repository.TransferOutAccountRepository;
/**
 * 
 * @author Fangfang.Xu
 */
@SpringBootApplication
@ComponentScan({"xff.test","github.easytcc"})
public class BankoutApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(BankoutApplication.class, args);
		
		TransferOutAccountRepository accountRepository = context.getBean(TransferOutAccountRepository.class);
		List<TransferOutAccount> findAll = accountRepository.findAll();
		if(findAll.isEmpty()){
			TransferOutAccount transferOutAccount = new TransferOutAccount();
			transferOutAccount.setAmt(1000000L);
			accountRepository.save(transferOutAccount);
		}
	}
}
