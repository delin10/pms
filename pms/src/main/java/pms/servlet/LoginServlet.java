package pms.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import pms.comm.DataWrapper;
import pms.dao.Dao;
import pms.util.RequestUtil;
import pms.util.comm.Info;

/**
 * Servlet implementation class Login
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String json=RequestUtil.getRequestBody(request);
		JSONObject form=JSONObject.parseObject(json);
		String id=form.getString("id");
		String pwd=form.getString("pwd");
		DataWrapper res=new DataWrapper();
		Info info = Dao.login(id, pwd);
		response.addCookie(new Cookie("token", info.getData().toString()));
		res.wrapInfo(info);
		response.getWriter().write(JSON.toJSONString(res));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
