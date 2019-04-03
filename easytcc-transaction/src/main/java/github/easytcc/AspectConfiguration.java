package github.easytcc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Configuration
public class AspectConfiguration {
	
	@Bean
	public TransactionAspect transactionAspect() {
		return new TransactionAspect();
	}
}
