package xff.bankout;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.web.client.RestTemplate;

import xff.test.Exceptions;
import xff.test.dto.TransferAmtDto;

/**
 * @author Fangfang.Xu
 *
 */
public class ConcurrentTransfer {

	static ExecutorService threadPool = Executors.newFixedThreadPool(10);
	
	static RestTemplate restTemplate = new RestTemplate();
	
	public static void transferOut(Exceptions exceptions,int transferCount) throws Exception {
		AtomicInteger success = new AtomicInteger();
		AtomicInteger fail = new AtomicInteger();
		AtomicInteger total = new AtomicInteger();
		
		ReentrantLock lock = new ReentrantLock();
		Condition condition = lock.newCondition();
		for(int i=0;i<transferCount;i++){
			threadPool.execute(new Runnable(){
				@Override
				public void run() {
					try{
						TransferAmtDto dto = new TransferAmtDto();
						dto.setAmt(100L);
						dto.setTransferNumber(UUID.randomUUID().toString());
						dto.setExceptions(exceptions);
						
						restTemplate.postForObject("http://localhost:8080/test-transferout", dto, Void.class);
						success.incrementAndGet();
					}catch (Exception e) {
						fail.incrementAndGet();
					}
					int incrementAndGet = total.incrementAndGet();
					if(incrementAndGet == transferCount){
						lock.lock();
						condition.signal();
						lock.unlock();
					}
				}
			});
		}
		lock.lock();
		condition.await();
		System.out.println("successed:"+success);
		System.out.println("failed:"+fail);
	}
}
