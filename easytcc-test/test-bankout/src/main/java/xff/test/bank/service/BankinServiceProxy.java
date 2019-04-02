package xff.test.bank.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import xff.test.TccHttpHeadersBuilder;
import xff.test.dto.TransferAmtDto;

/**
 * @author Fangfang.Xu
 *
 */
@Service
public class BankinServiceProxy {
	
	RestTemplate restTemplate = new RestTemplate();

	public void tranferToBankin(TransferAmtDto transferOutDto){
		HttpHeaders httpHeaders = TccHttpHeadersBuilder.build();
		HttpEntity httpEntity = new HttpEntity<>(transferOutDto, httpHeaders);
		restTemplate.exchange("http://localhost:8082/transferin", HttpMethod.POST, httpEntity, String.class);
	}
}
