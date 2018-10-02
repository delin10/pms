package pms.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import pms.comm.DataWrapper;
import pms.dao.Dao;
import pms.util.auth.manager.Session;
import pms.util.comm.Info;

/**
 * Servlet implementation class InitServlet
 */
public class InitServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		DataWrapper res=new DataWrapper();
		String comp=request.getParameter("comp");
		Session session=(Session)request.getAttribute("session");
		if ("tree_menu".equals(comp)) {
			Info info=Dao.resources_tree(session);
			res.wrapInfo(info);
			res.setData(info.getData());
		}else if ("page_menu".equals(comp)) {
			String id=request.getParameter("id");
			Info info=Dao.resources_tab(session, id);
			res.wrapInfo(info);
			res.setData(info.getData());
		}
		
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
