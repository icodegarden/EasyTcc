package xff.test.score.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import xff.test.score.entity.ScoreAccount;
/**
 * 
 * @author Fangfang.Xu
 */
public interface ScoreAccountRepository extends JpaRepository<ScoreAccount, Long>{

	ScoreAccount findByBankOutAccountId(Long id);
	
	ScoreAccount findByBankInAccountId(Long id);
	
	@Query(value="update score_acct set score = score + (?2) where id = ?1",nativeQuery=true)
	@Modifying
	int sumScore(long id,long score);
	
}
