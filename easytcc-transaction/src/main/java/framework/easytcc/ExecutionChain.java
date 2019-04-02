package framework.easytcc;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public interface ExecutionChain {

	public void preHandle(ProceedingJoinPointWrapper pointWrapper,
			ExecutionChain chain) throws Exception;

}
