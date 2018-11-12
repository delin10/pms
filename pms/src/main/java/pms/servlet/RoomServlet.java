package pms.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import pms.bean.Room;
import pms.comm.DataWrapper;
import pms.dao.Dao;
import pms.util.auth.manager.Session;

public class RoomServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		DataWrapper res = new DataWrapper();
		Session session=(Session) request.getAttribute("session");
		String json = (String) request.getAttribute("json");
		String action = request.getParameter("action");
		if ("add".equals(action)) {
			Room room = JSON.parseObject(json, Room.class);
			if (Dao.addRoom(room))
				response.getWriter().write("��ӳɹ�");
			else
				response.getWriter().write("���ʧ��");
		} else if ("update".equals(action)) {
			Room room = JSON.parseObject(json, Room.class);
			if (Dao.updateRoom(room))
				response.getWriter().write("���³ɹ�");
			else
				response.getWriter().write("����ʧ��");
		} else if ("query".equals(action)) {
			res.setData(Dao.queryRoom(session));
			res.suc("success");
		}
		response.getWriter().write(JSON.toJSONString(res));
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
