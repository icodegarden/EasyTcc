package xff.test.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import xff.test.bank.entity.TransferInOrder;
/**
 * 
 * @author Fangfang.Xu
 */
public interface TransferInOrderRepository extends JpaRepository<TransferInOrder, Long>{

	TransferInOrder findByTransferNumber(String number);
}
