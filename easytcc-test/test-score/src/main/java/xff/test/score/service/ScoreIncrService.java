package xff.test.score.service;

import xff.test.dto.ScoreIncrDto;
import xff.test.score.entity.ScoreIncrOrder;
/**
 * 
 * @author Fangfang.Xu
 */
public interface ScoreIncrService {

	ScoreIncrOrder scoreIncr(ScoreIncrDto scoreIncrDto);
}
