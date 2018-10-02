package pms.servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import pms.comm.DataWrapper;
import pms.dao.Dao;
import pms.util.auth.Getter;
import pms.util.auth.bean.Role;
import pms.util.auth.manager.Session;
import pms.util.comm.Info;

/**
 * Servlet implementation class SystemServlet
 */
public class SystemServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DataWrapper data=new DataWrapper();
		String json=(String) request.getAttribute("body");
		String action =request.getParameter("action");
		String role_id=request.getParameter("role_id");
		String user_id=request.getParameter("user_id");
		Session session=(Session)request.getAttribute("session");
		//System.out.println(request.getAttribute("my")+""+session);
		
		if ("setRole".equalsIgnoreCase(action)) {
			Info info=Dao.setRole(session, user_id, role_id);
			data.wrapInfo(info);
		}else if ("showUser".equalsIgnoreCase(action)) {
			data.wrapInfo(Dao.showUsers());
			data.suc("成功查询");
		}else if ("auth".equals(action)) {
			@SuppressWarnings("unchecked")
			ArrayList<String> list=JSON.parseObject(json, ArrayList.class);
			Info info=Dao.auth(session, list);
			data.wrapInfo(info);
			data.setData(info.getData());
		}else if ("roles".equals(action)) {
			Info info=Dao.roles();
			data.wrapInfo(info);
		}else if ("addRole".equalsIgnoreCase(action)) {
			Role role=JSON.parseObject(json,Role.class);
			data.wrapInfo(Getter.rolem.addRole(role));
		}else if ("delRole".equalsIgnoreCase(action)) {
			data.wrapInfo(Getter.rolem.delRole(role_id));
		}else {
			data.fail("功能尚未开放");
		}
		response.getWriter().write(JSON.toJSONString(data));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
