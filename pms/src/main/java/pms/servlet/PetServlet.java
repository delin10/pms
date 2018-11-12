package pms.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import pms.bean.Pet;
import pms.dao.Dao;
import pms.util.RequestUtil;

public class PetServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("body", RequestUtil.getRequestBody(request));
		String action = request.getParameter("action");
		if ("add".equals(action)) {
			String json = (String) request.getAttribute("body");
			Pet pet = JSON.parseObject(json, Pet.class);
			if (Dao.addPet(pet))
				response.getWriter().write("添加成功");
			else
				response.getWriter().write("添加失败");
		} else if ("update".equals(action)) {
			String json = (String) request.getAttribute("body");
			Pet pet = JSON.parseObject(json, Pet.class);
			if (Dao.updatePet(pet))
				response.getWriter().write("更新成功");
			else
				response.getWriter().write("更新失败");
		} else if ("delete".equals(action)) {
			String owner_id = request.getParameter("owner_id");
			String pet_id = request.getParameter("pet_id");
			if (Dao.deletePet(owner_id, pet_id)) {
				response.getWriter().write("删除成功");
			} else
				response.getWriter().write("删除失败");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
