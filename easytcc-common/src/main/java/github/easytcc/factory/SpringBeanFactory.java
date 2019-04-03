package github.easytcc.factory;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
/**
 * @author Fangfang.Xu
 *
 */
@Component
public class SpringBeanFactory implements ApplicationContextAware{

	protected static ApplicationContext ac;	
	
	@Override
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		SpringBeanFactory.ac = ac;
	}
	
	public static Object getBean(String name){
		return ac.getBean(name);
	}
	
	public static <T>T getBean(Class<T> c){
		return ac.getBean(c);
	}
}
