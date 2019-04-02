package xff.test;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import framework.easytcc.support.http.ContextHttpFilter;
/**
 * 
 * @author Fangfang.Xu
 */
@Configuration
public class ContextHttpFilterConfig {

	@Bean
    public FilterRegistrationBean contextHttpFilterRegistrationBean() {
    	FilterRegistrationBean registration = new FilterRegistrationBean();  
        registration.setFilter(new ContextHttpFilter());  
        registration.addUrlPatterns("/*");  
        registration.setName("contextHttpFilter");  
        return registration;  
    }
}
