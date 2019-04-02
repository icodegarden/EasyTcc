package framework.easytcc.transaction;


import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import framework.easytcc.exception.NeverBeRecoveryException;
import framework.easytcc.factory.SpringBeanFactory;

/**
 * @author Fangfang.Xu
 *
 */
public class LocalParticipant implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private ParticipantInvoker confirmer;
	
	private ParticipantInvoker canceller;
	
	private LocalParticipant(){}
	
	public static Builder builder() {
		return new Builder(new LocalParticipant());
	}
	
	public static class Builder{
		LocalParticipant localParticipant;
		Builder(LocalParticipant localParticipant){
			this.localParticipant = localParticipant;
		}
		public Builder confirmer(String className,String methodName,Object[] args){
			localParticipant.confirmer = new ParticipantInvoker(className, methodName, args);
			return this;
		}
		public Builder canceller(String className,String methodName,Object[] args){
			localParticipant.canceller = new ParticipantInvoker(className, methodName, args);
			return this;
		}
		public LocalParticipant build() {
			return localParticipant;
		}
	}
	
//	public static LocalParticipant get(){
//		return new LocalParticipant();
//	}

//	public LocalParticipant committer(String className,String methodName,Object[] args){
//		this.confirmer = new ParticipantInvoker(className, methodName, args);
//		return this;
//	}
//	
//	public LocalParticipant canceller(String className,String methodName,Object[] args){
//		canceller = new ParticipantInvoker(className, methodName, args);
//		return this;
//	}
	
	public void confirm() throws NeverBeRecoveryException,InvocationTargetException {
		confirmer.invoke();
	}

	public void cancel() throws NeverBeRecoveryException,InvocationTargetException {
		canceller.invoke();
	}

	private static class ParticipantInvoker implements Serializable{

		private static final long serialVersionUID = 1L;
		
		private String className;
		
		private String methodName;
		
		private Object[] args;
		
		public ParticipantInvoker(String className, String methodName,Object[] args) {
			this.className = className;
			this.methodName = methodName;
			this.args = args;
		}
		/**
		 * @return
		 * @throws NeverBeRecoveryException	these exceptions can not recovery
		 * @throws InvocationTargetException 
		 */
		public Object invoke() throws NeverBeRecoveryException, InvocationTargetException{
			try {
				Class<?> targetClass = Class.forName(className);
				Object targetBean = SpringBeanFactory.getBean(targetClass);
				Method[] methods = targetClass.getDeclaredMethods();
				for(Method m:methods){
					if(m.getName().equals(methodName)){
						return m.invoke(targetBean, args);
					}
				}
				throw new NeverBeRecoveryException("Participant invoke failed,no such method,className:"+
						className+",methodName:"+methodName+",args:"+Arrays.toString(args));
			} catch (ReflectiveOperationException e) {
				if(e instanceof InvocationTargetException){
					throw (InvocationTargetException)e;
				}
				throw new NeverBeRecoveryException("Participant invoke failed,className:"+
						className+",methodName:"+methodName+",args:"+Arrays.toString(args),e);
			}
		}
	}
}
