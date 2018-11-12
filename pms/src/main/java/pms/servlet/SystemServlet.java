package pms.servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import pms.comm.DataWrapper;
import pms.dao.Dao;
import pms.util.auth.bean.Role;
import pms.util.auth.manager.Session;
import pms.util.comm.Info;

/**
 * Servlet implementation class SystemServlet
 */
public class SystemServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		DataWrapper data = new DataWrapper();
		String json = (String) request.getAttribute("body");
		String action = request.getParameter("action");
		String role_id = request.getParameter("role_id");
		String user_id = request.getParameter("user_id");
		Session session = (Session) request.getAttribute("session");
		// System.out.println(request.getAttribute("my")+""+session);
		if ("setRole".equalsIgnoreCase(action)) {
			Info info = Dao.setRole(user_id, role_id);
			data.wrapInfo(info);
		} else if ("showUser".equalsIgnoreCase(action)) {
			data.wrapInfo(Dao.showUsers());
			data.suc("成功查询");
		} else if ("auth".equals(action)) {
			@SuppressWarnings("unchecked")
			ArrayList<String> list = JSON.parseObject(json, ArrayList.class);
			Info info = Dao.auth(session, list, role_id);
			data.wrapInfo(info);
			data.setData(info.getData());
		} else if ("roles".equals(action)) {
			Info info = Dao.roles();
			data.wrapInfo(info);
		} else if ("addRole".equalsIgnoreCase(action)) {
			Role role = JSON.parseObject(json, Role.class);
			data.wrapInfo(Dao.addRole(role));
		} else if ("delRole".equalsIgnoreCase(action)) {
			data.wrapInfo(Dao.delRole(role_id));
		} else if ("resources".equalsIgnoreCase(action)) {
			data.wrapInfo(Dao.allResources(role_id));
		} else if ("updateRole".equalsIgnoreCase(action)) {
			try {
				Role role = JSON.parseObject(json, Role.class);
				data.wrapInfo(Dao.updateRole(role));
			} catch (Exception e) {
				data.fail("数据有误，更新失败");
			}
		}else if ("search".equals(action)){
			String subject=request.getParameter("subject");
			String word=request.getParameter("word");
			String key=request.getParameter("key");
			data.wrapInfo(Dao.search(subject, key, word));
		}else if ("backups".equalsIgnoreCase(action)){
			data.wrapInfo(Dao.backups());
		}else if ("backup".equalsIgnoreCase(action)){
			data.wrapInfo(Dao.backup());
		}else if ("delBackup".equalsIgnoreCase(action)){
			@SuppressWarnings("unchecked")
			ArrayList<String> list = JSON.parseObject(json, ArrayList.class);
			data.wrapInfo(Dao.delBackup(list));
		}else if("restore".equalsIgnoreCase(action)){
			String path=JSONObject.parseObject(json).getString("path");
			data.wrapInfo(Dao.restore(path));
		}else {
			data.fail("功能尚未开放");
		}
		response.getWriter().write(JSON.toJSONString(data));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
