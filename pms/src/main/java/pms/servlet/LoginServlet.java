package pms.servlet;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import pms.comm.DataWrapper;
import pms.comm.RuntimeStorage;
import pms.dao.Dao;
import pms.util.RequestUtil;
import pms.util.auth.manager.Session;
import pms.util.comm.Info;
import pms.util.comm.lambda.StreamUtil;

/**
 * Servlet implementation class Login
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String json = RequestUtil.getRequestBody(request);
		String action = request.getParameter("action");
		DataWrapper res = new DataWrapper();
		Cookie[] cookies = request.getCookies();
		System.out.println(json);
		// Arrays.stream(cookies==null?new
		// Cookie[0]:cookies).map(cookie->cookie.getName()+"="+cookie.getValue()).forEach(System.out::println);
		if ("check".equalsIgnoreCase(action)) {
			if (cookies == null) {
				res.fail("cookie is null");
			} else {
				Iterator<Cookie> it = StreamUtil.find(request.getCookies(), "token", Cookie::getName).iterator();
				// System.out.println(it.hasNext());
				// =¡·true
				if (it.hasNext()) {
					Cookie cookie = it.next();
					// System.out.println(cookie.getName()+"="+cookie.getValue());
					// =¡·ok
					Session session = RuntimeStorage.getGetter().getUserManager().check(cookie.getValue());
					if (session != null) {
						cookie.setMaxAge(1600);
						response.addCookie(cookie);
						res.suc("valid");
						res.setData(session.getAttributes("_basic_"));
					} else {
						res.fail("expired");
					}
				} else {
					res.fail("no token");
				}
			}
		} else {
			JSONObject form = JSONObject.parseObject(json);
			String id = form.getString("id");
			String pwd = form.getString("pwd");
			Info info = Dao.login(id, pwd);
			Session session = (Session) info.getData();
			if (session != null) {
				Cookie token = new Cookie("token", session.getSession_id());
				token.setMaxAge(1600);
				response.addCookie(token);
				res.wrapInfo(info);
				res.setData(session.getAttributes("_basic_"));
			} else {
				res.fail("fail");
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
