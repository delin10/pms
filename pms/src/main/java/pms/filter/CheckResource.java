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
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import pms.comm.DataWrapper;
import pms.util.RequestUtil;
import pms.util.auth.Getter;
import pms.util.auth.manager.Session;
import pms.util.auth.manager.UserManager;
import pms.util.comm.Info;
import pms.util.comm.lambda.StreamUtil;

/**
 * Servlet Filter implementation class CheckResource
 */
public class CheckResource implements Filter {

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
		System.out.println("过滤器被销毁");
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)	
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		request.setAttribute("body", RequestUtil.getRequestBody(req));
		DataWrapper data = new DataWrapper();
		Cookie[] cookies = req.getCookies();
		response.setCharacterEncoding("GBK");
//		Iterator<Cookie> it = StreamUtil.find(cookies==null?new Cookie[0]:cookies, "token", Cookie::getName).iterator();
//		if (it.hasNext()) {
//			Cookie cookie = it.next();
//			UserManager um = Getter.um;
//			Session session = um.check(cookie.getValue());
//			request.setAttribute("session", session);
//			String resource_id = request.getParameter("resource_id");
//			if (session != null) {
//				Info info = um.getPermission(session, resource_id);
//				if (info.getStatus() == 0) {
//					chain.doFilter(request, response);
//				} else {
//					data.fail("权限不足");
//					// 尝试把他放到结尾
//					RequestUtil.write(res, JSON.toJSONString(data), "GBK");
//				}
//			} else {
//				data.fail("用户未登录");
//				// 尝试把他放到结尾
//				RequestUtil.write(res, JSON.toJSONString(data), "GBK");
//			}
//		} else {
//			data.fail("该用户不存在");
//			// 尝试把他放到结尾
//			RequestUtil.write(res, JSON.toJSONString(data), "GBK");
//		}

	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
