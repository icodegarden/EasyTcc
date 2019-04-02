package xff.test.score.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * 
 * @author Fangfang.Xu
 */
@Entity
@Table(name="score_acct")
public class ScoreAccount implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private Long bankOutAccountId;
	
	private Long bankInAccountId;
	
	private Long score;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBankOutAccountId() {
		return bankOutAccountId;
	}

	public void setBankOutAccountId(Long bankOutAccountId) {
		this.bankOutAccountId = bankOutAccountId;
	}

	public Long getBankInAccountId() {
		return bankInAccountId;
	}

	public void setBankInAccountId(Long bankInAccountId) {
		this.bankInAccountId = bankInAccountId;
	}

	public Long getScore() {
		return score;
	}

	public void setScore(Long score) {
		this.score = score;
	}


}
