package pms.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import pms.service.BasicService;
import pms.service.impl.BackupServiceImpl;
import pms.util.auth.manager.Session;
import pms.util.comm.Info;

public class BackupServlet extends BasicServiceServlet {
	private static final long serialVersionUID = -6350214405113454859L;

	@Override
	public BasicService getService() {
		// TODO Auto-generated method stub
		return new BackupServiceImpl();
	}

	@Override
	protected Info add(BasicService service, HttpServletRequest request, HttpServletResponse response) {
		Session session = (Session) request.getAttribute("session");
		return service.add(session, null);
	}

	@SuppressWarnings("unchecked")
	protected Info delete(BasicService service, HttpServletRequest request, HttpServletResponse response) {
		Session session = (Session) request.getAttribute("session");
		String json = (String) request.getAttribute("body");
		Map<String, String>[] paths = JSON.parseObject(json, HashMap[].class);
		return service.delete(session, paths);
	}

	protected Info update(BasicService service, HttpServletRequest request, HttpServletResponse response) {
		Session session = (Session) request.getAttribute("session");
		String json = (String) request.getAttribute("body");
		@SuppressWarnings("unchecked")
		Map<String, String> path = JSON.parseObject(json, HashMap.class);
		return service.update(session, path, null);
	}
}
