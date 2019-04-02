package framework.easytcc;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class AspectExecutionChain {

	private static final Logger logger = LoggerFactory.getLogger(AspectExecutionChain.class);

	private TransactionAspectInterceptor[] interceptors;

	public AspectExecutionChain() {
		this((TransactionAspectInterceptor[]) null);
	}

	public AspectExecutionChain(TransactionAspectInterceptor... interceptors) {
		sortAndReset(interceptors);
	}

	public void addInterceptor(TransactionAspectInterceptor interceptor) {
		addInterceptors(interceptor);
	}

	public void addInterceptors(TransactionAspectInterceptor... interceptors) {
		if (!ObjectUtils.isEmpty(interceptors)) {
			int originLength = this.interceptors.length;
			TransactionAspectInterceptor[] copyOf = Arrays.copyOf(this.interceptors,
					originLength + interceptors.length);
			for (TransactionAspectInterceptor interceptor : interceptors) {
				copyOf[originLength++] = interceptor;
			}
			sortAndReset(copyOf);
		}
	}

	private void sortAndReset(TransactionAspectInterceptor[] interceptors) {
		List<TransactionAspectInterceptor> asList = Arrays.asList(interceptors);
		asList.sort(new Comparator<TransactionAspectInterceptor>() {
			@Override
			public int compare(TransactionAspectInterceptor o1, TransactionAspectInterceptor o2) {
				if (o1.getOrder() <= o2.getOrder()) {
					return -1;
				}
				if (o1.getOrder() == o2.getOrder()) {
					return 0;
				}
				return 1;
			}
		});
		this.interceptors = asList.toArray(new TransactionAspectInterceptor[asList.size()]);
	}

	public TransactionAspectInterceptor[] getInterceptors() {
		return this.interceptors;
	}

	void applyPreHandle(ProceedingJoinPointWrapper pointWrapper) throws Exception {
		ExecutionChain chain = new DefaultExecutionChain();
		chain.preHandle(pointWrapper, chain);
	}

	void applyPostHandle(ProceedingJoinPointWrapper pointWrapper) throws Exception {
		TransactionAspectInterceptor[] interceptors = getInterceptors();
		if (!ObjectUtils.isEmpty(interceptors)) {
			for (int i = 0; i < interceptors.length; i++) {
				TransactionAspectInterceptor interceptor = interceptors[i];
				interceptor.postHandle(pointWrapper);
			}
		}
	}

	void triggerAfterHandleCompletion(ProceedingJoinPointWrapper pointWrapper, Throwable e) throws Exception {
		TransactionAspectInterceptor[] interceptors = getInterceptors();
		if (!ObjectUtils.isEmpty(interceptors)) {
			for (int i = 0; i < interceptors.length; i++) {
				TransactionAspectInterceptor interceptor = interceptors[i];
				try {
					interceptor.afterCompletion(pointWrapper, e);
				} catch (Throwable ex2) {
					logger.error("TransactionInterceptor.afterCompletion threw exception", ex2);
				}
			}
		}
	}

	class DefaultExecutionChain implements ExecutionChain {
		private int index;

		@Override
		public void preHandle(ProceedingJoinPointWrapper pointWrapper, ExecutionChain chain) throws Exception {
			TransactionAspectInterceptor[] interceptors = getInterceptors();
			if (index == interceptors.length) {
				return;
			}
			TransactionAspectInterceptor interceptor = interceptors[index++];
			interceptor.preHandle(pointWrapper, chain);
		}
	}

	@Override
	public String toString() {
		return Arrays.toString(interceptors);
	}

}
