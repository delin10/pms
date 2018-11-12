package pms.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;

import pms.comm.DataWrapper;
import pms.comm.RuntimeStorage;
import pms.util.auth.manager.Session;
import pms.util.comm.Info;

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
		//System.out.println("CheckResourceFilter");
		HttpServletRequest req = (HttpServletRequest) request;
		DataWrapper res = new DataWrapper();
		Session session = (Session) req.getAttribute("session");
		//System.out.println(session);
		request.setAttribute("session", session);
		String resource_id = request.getParameter("resource_id");
		Info info =RuntimeStorage.getGetter().getUserManager().getPermission(session, resource_id);
		if (info.getStatus() == 0) {
			chain.doFilter(request, response);
		} else {
			res.fail("权限不足");
			// 尝试把他放到结尾
			response.getWriter().write(JSON.toJSONString(res));
			return;
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
