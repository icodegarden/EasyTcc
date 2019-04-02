package framework.easytcc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import framework.easytcc.annotation.EasyTcc;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class ProceedingJoinPointWrapper {
	
	private Map<String, Object> metadata = new HashMap<String,Object>(0);
	
	private ProceedingJoinPoint pjp;
	
	private Method method;
	
	private EasyTcc annotation;
	
	private boolean xidCreateStack;
	
	private boolean localTransactionBeginStack;
	
	public ProceedingJoinPointWrapper(ProceedingJoinPoint pjp){
		this.pjp = pjp;
		method = compensableMethod(pjp);
		annotation = method.getAnnotation(EasyTcc.class);
	}
	
	private Method compensableMethod(ProceedingJoinPoint pjp) {
        Method method = ((MethodSignature) (pjp.getSignature())).getMethod();

        if (method.getAnnotation(EasyTcc.class) == null) {
            try {
                method = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                return null;
            }
        }
        return method;
    }
	
	public void putMetadata(String key,Object value) {
		metadata.put(key, value);
	}
	
	public Object getMetadata(String key) {
		return metadata.get(key);
	}
	
	void markeXidCreateStack() {
		this.xidCreateStack = true;
	}
	
	public boolean isXidCreateStack() {
		return xidCreateStack;
	}
	
	void markeLocalTransactionBeginStack() {
		this.localTransactionBeginStack = true;
	}
	
	public boolean isLocalTransactionBeginStack() {
		return localTransactionBeginStack;
	}
	
	public String className(){
		return pjp.getTarget().getClass().getName();
	}
	
	public Method method(){
		return method;
	}
	
	public Object[] args(){
		return pjp.getArgs();
	}
	
	public EasyTcc annotation(){
		return annotation;
	}
	
	public String action(){
		return annotation.action();
	}
	
	public String confirmMethodName(){
		return annotation.confirmMethod();
	}
	
	public String cancelMethodName(){
		return annotation.cancelMethod();
	}

	public ProceedingJoinPoint getProceedingJoinPoint() {
		return pjp;
	}
	
}
