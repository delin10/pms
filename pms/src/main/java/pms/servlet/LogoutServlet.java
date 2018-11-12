package pms.servlet;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import pms.comm.DataWrapper;
import pms.comm.RuntimeStorage;
import pms.dao.Dao;
import pms.util.auth.manager.Session;
import pms.util.auth.manager.UserManager;
import pms.util.comm.lambda.StreamUtil;

/**
 * Servlet implementation class LogoutServlet
 */
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LogoutServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		DataWrapper res = new DataWrapper();
		Cookie[] cookies = request.getCookies();
		Iterator<Cookie> it = StreamUtil.find(cookies == null ? new Cookie[0] : cookies, "token", Cookie::getName)
				.iterator();
		res.suc("");
		if (it.hasNext()) {
			Cookie cookie = it.next();
			UserManager um =RuntimeStorage.getGetter().getUserManager();
			Session session = um.check(cookie.getValue());
			if (session != null) {
				res.wrapInfo(Dao.logout(session));
			}
		}
		response.getWriter().write(JSON.toJSONString(res));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
