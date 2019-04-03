package github.easytcc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Fangfang.Xu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface EasyTcc {
	
	/**
	 * transaction description
	 */
	public String action() default "";
	
	public String confirmMethod() default "";

    public String cancelMethod() default "";
}