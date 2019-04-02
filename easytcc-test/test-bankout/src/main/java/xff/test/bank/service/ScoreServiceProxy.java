package xff.test.bank.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import xff.test.TccHttpHeadersBuilder;
import xff.test.dto.ScoreIncrDto;

/**
 * @author Fangfang.Xu
 *
 */
@Service
public class ScoreServiceProxy {
	
	RestTemplate restTemplate = new RestTemplate();
	
	public void scoreincr(ScoreIncrDto scoreIncrDto){
		HttpHeaders httpHeaders = TccHttpHeadersBuilder.build();
		HttpEntity httpEntity = new HttpEntity(scoreIncrDto, httpHeaders);
		restTemplate.exchange("http://localhost:8083/scoreincr", HttpMethod.POST, httpEntity, String.class);
	}
}
