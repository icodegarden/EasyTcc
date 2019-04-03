package github.easytcc.support.http;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import github.easytcc.context.TransactionContextHolder;
import github.easytcc.context.TransactionContextImpl;
import github.easytcc.context.Xid;
import github.easytcc.support.ContextHeaders;

/**
 * @author Fangfang.Xu
 *
 */
public class ContextHttpFilter implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		String id = req.getHeader(ContextHeaders.XID);
		String upstreamServer = req.getHeader(ContextHeaders.APPLICTION);
		String parentId = req.getHeader(ContextHeaders.TRANSACTIONID);
		
		if(id != null){
			Xid xid = new Xid(id,null);
			TransactionContextHolder.setContext(new TransactionContextImpl(xid, upstreamServer, parentId));
		}
		try{
			chain.doFilter(request, response);
		}finally {
			TransactionContextHolder.clearContext();
		}
	}

	@Override
	public void destroy() {
		
	}

}
