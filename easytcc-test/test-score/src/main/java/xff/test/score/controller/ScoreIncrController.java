package xff.test.score.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import xff.test.dto.ScoreIncrDto;
import xff.test.score.entity.ScoreAccount;
import xff.test.score.entity.ScoreIncrOrder;
import xff.test.score.repository.ScoreAccountRepository;
import xff.test.score.service.ScoreIncrService;

/**
 * @author Fangfang.Xu
 *
 */
@RestController
public class ScoreIncrController {
	
	@Autowired
	ScoreIncrService scoreIncrService;
	@Autowired
	ScoreAccountRepository scoreAccountRepository;
	
	@RequestMapping(value="/scoreincr",method=RequestMethod.POST,produces="application/json")
	public ScoreIncrOrder scoreincr(@RequestBody ScoreIncrDto scoreIncrDto){
		ScoreIncrOrder scoreIncr = scoreIncrService.scoreIncr(scoreIncrDto);
		return scoreIncr;
	}
	
	@RequestMapping(value="/scores",method=RequestMethod.GET,produces="application/json")
	public List<ScoreAccount> scores(){
		List<ScoreAccount> findAll = scoreAccountRepository.findAll();
		return findAll;
	}
}
