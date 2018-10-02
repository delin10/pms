package pms.filter;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;

import pms.comm.DataWrapper;
import pms.util.RequestUtil;
import pms.util.auth.Getter;
import pms.util.auth.manager.Session;
import pms.util.auth.manager.UserManager;
import pms.util.comm.lambda.StreamUtil;

/**
 * Servlet Filter implementation class LoginFilter
 */
public class LoginFilter implements Filter {
	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("½øÈëLoginFilter");
		DataWrapper res=new DataWrapper();
		HttpServletRequest req = (HttpServletRequest) request;
		request.setAttribute("body", RequestUtil.getRequestBody(req));
		Cookie[] cookies = req.getCookies();
		Iterator<Cookie> it = StreamUtil.find(cookies == null ? new Cookie[0] : cookies, "token", Cookie::getName)
				.iterator();
		if (it.hasNext()) {
			Cookie cookie = it.next();
			UserManager um = Getter.um;
			Session session = um.check(cookie.getValue());
			if (session == null) {
				res.fail("Î´µÇÂ¼");
				response.getWriter().write(JSON.toJSONString(res));
				return;
			}
			request.setAttribute("session", session);
		}
		//DHttpRequestWrapper wrapper=new DHttpRequestWrapper(req);
		chain.doFilter(request, response);

	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
