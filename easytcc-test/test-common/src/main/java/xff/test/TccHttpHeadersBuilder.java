package xff.test;

import org.springframework.http.HttpHeaders;

import framework.easytcc.support.ContextHeaders;
import framework.easytcc.support.ContextHeaders.KeyValuePair;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class TccHttpHeadersBuilder {

	public static HttpHeaders build() {
		HttpHeaders httpHeaders = new HttpHeaders();

		ContextHeaders headers = ContextHeaders.get();
		while (headers.hasNext()) {
			KeyValuePair pair = headers.next();
			httpHeaders.add(pair.getKey(), pair.getValue());
		}
		
		return httpHeaders;
	}
}
