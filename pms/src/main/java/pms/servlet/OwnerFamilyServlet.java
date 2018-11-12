package pms.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import pms.bean.Owner_family;
import pms.dao.Dao;
import pms.util.RequestUtil;


public class OwnerFamilyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
   
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 request.setAttribute("body", RequestUtil.getRequestBody(request));
		 String  action = request.getParameter("action");
		   if("add".equals(action)) {
			   String json = (String)request.getAttribute("body");
			   Owner_family owner_family = JSON.parseObject(json, Owner_family.class);
			   if(Dao.addOwnerFamily(owner_family))
				   response.getWriter().write("��ӳɹ�");
			   else
				   response.getWriter().write("���ʧ��");
		   }
		   else if("update".equals(action)) {
			   String json = (String)request.getAttribute("body");
			   Owner_family owner_family = JSON.parseObject(json, Owner_family.class);
			   if(Dao.updateOwnerFamily(owner_family))
				   response.getWriter().write("���³ɹ�");
			   else
				   response.getWriter().write("����ʧ��");
		   }
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
