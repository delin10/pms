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
import pms.service.BasicService;
import pms.util.auth.manager.Session;
import pms.util.comm.Info;

public abstract class BasicServiceServlet extends HttpServlet {
	private static final long serialVersionUID = -3390142031730955468L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		BasicService service = getService();
		DataWrapper res = new DataWrapper();
		String action = request.getParameter("action");
		if ("add".equals(action)) {
			res.wrapInfo(add(service, request, response));
		} else if ("update".equals(action)) {
			res.wrapInfo(update(service, request, response));
		} else if ("query".equals(action)) {
			res.wrapInfo(query(service, request, response));
		} else if ("delete".equals(action)) {
			res.wrapInfo(delete(service, request, response));
		} else {
			doServiceChain(service, request, res);
		}
		response.getWriter().write(JSON.toJSONString(res));
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	protected Info query(BasicService service, HttpServletRequest request, HttpServletResponse response) {
		Session session = (Session) request.getAttribute("session");
		return service.query(session);
	}

	@SuppressWarnings("unchecked")
	protected Info add(BasicService service, HttpServletRequest request, HttpServletResponse response) {
		Session session = (Session) request.getAttribute("session");
		String json = (String) request.getAttribute("body");
		Map<String, String> building = JSON.parseObject(json, HashMap.class);
		return service.add(session, building);
	}

	@SuppressWarnings("unchecked")
	protected Info delete(BasicService service, HttpServletRequest request, HttpServletResponse response) {
		Session session = (Session) request.getAttribute("session");
		String json = (String) request.getAttribute("body");
		System.out.println(json);
		HashMap<String, String>[] maps = JSON.parseObject(json, HashMap[].class);
		return service.delete(session, maps);
	}

	@SuppressWarnings("unchecked")
	protected Info update(BasicService service, HttpServletRequest request, HttpServletResponse response) {
		Session session = (Session) request.getAttribute("session");
		String json = (String) request.getAttribute("body");
		System.out.println(json);
		Map<String, String> values = JSON.parseObject(json, HashMap.class);
		Map<String, String> keys = new HashMap<>();
		request.getParameterMap().entrySet().stream()
				.filter(entry -> !entry.getKey().equals("action") && !entry.getKey().equals("type"))
				.forEach(entry -> keys.put(entry.getKey(), entry.getValue()[0]));
		return service.update(session, values, keys);
	}

	public abstract BasicService getService();

	public void doServiceChain(BasicService service, HttpServletRequest request, DataWrapper res) {
	}
}
