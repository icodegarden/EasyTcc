package github.easytcc.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import github.easytcc.AspectExecutionChain;
import github.easytcc.TransactionAspect;
import github.easytcc.repository.XidRepository;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Configuration
public class AspectConfiguration {
	
	private static final Logger logger = LoggerFactory.getLogger(AspectConfiguration.class);
	
	@Bean
	public TransactionAspect transactionAspect(AspectExecutionChain chain, XidRepository xidRepository,
			TccProperties tccProperties) {
		try {
			ResourceBanner resourceBanner = new ResourceBanner(new ClassPathResource("easytcc-banner.txt"));
			resourceBanner.printBanner(new StandardEnvironment(), null, System.out);
		} catch (Exception e) {
			logger.warn("print easytcc banner failed,case:{}",e.getMessage());
		}
		return new TransactionAspect(chain, xidRepository, tccProperties);
	}
	
	class ResourceBanner {

		public static final int BUFFER_SIZE = 4096;

		private Resource resource;

		public ResourceBanner(Resource resource) {
			Assert.notNull(resource, "Resource must not be null");
			Assert.isTrue(resource.exists(), "Resource must exist");
			this.resource = resource;
		}

		public void printBanner(Environment environment, Class<?> sourceClass,
				PrintStream out) {
			try {
				String banner = copyToString(this.resource.getInputStream(),
						environment.getProperty("spring.banner.charset", Charset.class,
								StandardCharsets.UTF_8));

				out.println(banner);
			}
			catch (Exception ex) {
				logger.warn("Banner not printable: " + this.resource + " (" + ex.getClass()
						+ ": '" + ex.getMessage() + "')", ex);
			}
		}
		public String copyToString(InputStream in, Charset charset) throws IOException {
			if (in == null) {
				return "";
			}
			StringBuilder out = new StringBuilder();
			InputStreamReader reader = new InputStreamReader(in, charset);
			char[] buffer = new char[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = reader.read(buffer)) != -1) {
				out.append(buffer, 0, bytesRead);
			}
			return out.toString();
		}
	}
}

