package pms.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import pms.comm.DataWrapper;
import pms.comm.RuntimeStorage;
import pms.util.RequestUtil;
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
		DataWrapper res = new DataWrapper();
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse response_ = (HttpServletResponse) response;
		System.out.println("################################");
		System.out.println("request中文件的长度为:"+request.getContentLength());
		System.out.println("request的类型为:"+request.getContentType());
		String type=request.getContentType();
		if ("multipart/form-data".equalsIgnoreCase(type)) {
			System.out.println("解析文件");
			request.setAttribute("body", RequestUtil.getBytes((HttpServletRequest) request,request.getContentLength()));
		}else if ("application/json".equalsIgnoreCase(type)) {
			request.setAttribute("body", RequestUtil.getRequestBody((HttpServletRequest) request));
		}
		System.out.println(req.getRequestURI()+"进入LoginFilter");
		Cookie[] cookies = req.getCookies();
		//Arrays.stream(cookies).map(cookie->cookie.getName()+"="+cookie.getValue()).forEach(System.out::println);
		System.out.println(req.getHeader("Cookie"));
		System.out.println(cookies);
		Iterator<Cookie> it = StreamUtil.find(cookies == null ? new Cookie[0] : cookies, "token", Cookie::getName)
				.iterator();
		if (it.hasNext()) {
			Cookie cookie = it.next();
			UserManager um = RuntimeStorage.getGetter().getUserManager();
			Session session = um.check(cookie.getValue());
			if (session == null) {
				res.msg("未登录", -2);
				response.getWriter().write(JSON.toJSONString(res));
				return;
			}
			cookie.setMaxAge(session.getEXPIRE_TIME());
			Arrays.stream(cookies).map(c->{
				c.setMaxAge(1600);
				return c;
			}).forEach(response_::addCookie);
			
			request.setAttribute("session", session);
		}else {
			res.msg("未登录", -2);
			response.getWriter().write(JSON.toJSONString(res));
			return;
		}
		// DHttpRequestWrapper wrapper=new DHttpRequestWrapper(req);
		chain.doFilter(request, response);

	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
