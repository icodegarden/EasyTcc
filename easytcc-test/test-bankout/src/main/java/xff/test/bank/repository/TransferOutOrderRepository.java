package xff.test.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import xff.test.bank.entity.TransferOutOrder;
/**
 * 
 * @author Fangfang.Xu
 */
public interface TransferOutOrderRepository extends JpaRepository<TransferOutOrder, Long>{

	TransferOutOrder findByTransferNumber(String number);
}
