package xff.test.bank.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import xff.test.bank.entity.TransferOutAccount;
import xff.test.bank.repository.TransferOutAccountRepository;
/**
 * 
 * @author Fangfang.Xu
 */
@Controller
public class IndexController {
	
	@Autowired
	TransferOutAccountRepository transferOutAccountRepository;
	
	@RequestMapping(value="/")
	public ModelAndView index(ModelAndView mv) throws JsonParseException, JsonMappingException, IOException{
		TransferOutAccount transferOutAccount = transferOutAccountRepository.findAll().get(0);
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders httpHeaders = new HttpHeaders();
		HttpEntity httpEntity = new HttpEntity<>(null, httpHeaders);
		String transferInAccountJson= restTemplate.exchange("http://localhost:8082/transferInAccount", HttpMethod.GET, httpEntity, String.class).getBody();
		Map transferInAccount = objectMapper.readValue(transferInAccountJson, Map.class);
		
		String scoresJson= restTemplate.exchange("http://localhost:8083/scores", HttpMethod.GET, httpEntity, String.class).getBody();
		List<Map<String,Object>> scores = objectMapper.readValue(scoresJson,List.class);
		
		mv.addObject("transferOutAccount", transferOutAccount);
		mv.addObject("transferInAccount", transferInAccount);
		for(Map<String,Object> score:scores){
			if(score.get("bankOutAccountId") != null){
				mv.addObject("transferOutScore", score);
			}
			if(score.get("bankInAccountId") != null){
				mv.addObject("transferInScore", score);
			}
		}
		mv.setViewName("index");
		return mv;
	}
}
