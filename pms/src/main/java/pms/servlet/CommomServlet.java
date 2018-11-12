package pms.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import pms.comm.DataWrapper;
import pms.dao.Dao2;
import pms.util.auth.manager.Session;
import pms.util.comm.Info;

/**
 * Servlet implementation class CommomServlet
 */
public class CommomServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CommomServlet() {
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
		String action = request.getParameter("action");
		String type = request.getParameter("type");
		Session session = (Session) request.getAttribute("session");
		String json = (String) request.getAttribute("body");

		if ("query".equals(action)) {
			Info info = Dao2.query(session, type);
			res.wrapInfo(info);
		} else if ("delete".equals(action)) {
			@SuppressWarnings("unchecked")
			HashMap<String,String>[] maps=JSON.parseObject(json,HashMap[].class); 
			Info info = Dao2.delete(session, type, maps);
			res.wrapInfo(info);
		} else if ("update".equals(action)) {
			@SuppressWarnings("unchecked")
			Map<String, String> values = JSON.parseObject(json, HashMap.class);
			Map<String, String> keys = new HashMap<>();
			request.getParameterMap().entrySet().stream()
					.filter(entry -> !entry.getKey().equals("action") && !entry.getKey().equals("type"))
					.forEach(entry -> keys.put(entry.getKey(), entry.getValue()[0]));
			System.out.println(keys);
			Info info = Dao2.update(session, values, type, keys);
			res.wrapInfo(info);
		} else if ("add".equals(action)) {
			@SuppressWarnings("unchecked")
			Map<String, String> values = JSON.parseObject(json, HashMap.class);
			Info info = Dao2.add(session, values, type);
			res.wrapInfo(info);
		}
		response.getWriter().write(JSON.toJSONString(res));
		;
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
