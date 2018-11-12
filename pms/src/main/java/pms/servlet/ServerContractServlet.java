package pms.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import pms.bean.Server_contract;
import pms.dao.Dao;
import pms.util.RequestUtil;

public class ServerContractServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
  
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 request.setAttribute("body", RequestUtil.getRequestBody(request));
		 String  action = request.getParameter("action");
		   if("add".equals(action)) {
			   String json = (String)request.getAttribute("body");
			   Server_contract serverContract = JSON.parseObject(json, Server_contract.class);
			   if(Dao.addServer_contract(serverContract))
				   response.getWriter().write("��ӳɹ�");
			   else
				   response.getWriter().write("���ʧ��");
		   }
		   else if("update".equals(action)) {
			   String json = (String)request.getAttribute("body");		  
			   Server_contract serverContract = JSON.parseObject(json, Server_contract.class);
			   if(Dao.updateServerContract(serverContract))
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
