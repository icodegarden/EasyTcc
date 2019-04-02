package xff.test;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import xff.test.bank.entity.TransferInAccount;
import xff.test.bank.repository.TransferInAccountRepository;

/**
 * 
 * @author Fangfang.Xu
 */
@SpringBootApplication
@ComponentScan({ "xff.test", "framework.easytcc" })
public class BankinApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(BankinApplication.class, args);

		TransferInAccountRepository accountRepository = context.getBean(TransferInAccountRepository.class);
		List<TransferInAccount> findAll = accountRepository.findAll();
		if (findAll.isEmpty()) {
			TransferInAccount transferInAccount = new TransferInAccount();
			transferInAccount.setAmt(0L);
			accountRepository.save(transferInAccount);
		}
	}
}
