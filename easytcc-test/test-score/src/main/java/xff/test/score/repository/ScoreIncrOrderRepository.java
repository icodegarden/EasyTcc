package xff.test.score.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import xff.test.score.entity.ScoreIncrOrder;
/**
 * 
 * @author Fangfang.Xu
 */
public interface ScoreIncrOrderRepository extends JpaRepository<ScoreIncrOrder, Long>{

	ScoreIncrOrder findByIncrOrderNumber(String number);
}
