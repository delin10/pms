package pms.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import pms.bean.Company;
import pms.dao.Dao;

/**
 * Servlet implementation class Company
 */
public class CompanyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		System.out.println(action);
		if("query".equals(action)) {
			String json=JSON.toJSONString(Dao.queryCompany());
			response.getWriter().write(json);
		}else if("update".equals(action)) {
			String json=(String) request.getAttribute("body");
            Company  company =  JSON.parseObject(json,Company.class);
            if(Dao.updateCompany(company))
            	  response.getWriter().write("更新成功");
			   else
				   response.getWriter().write("更新失败");
		}else if("company_image".equals(action)){
			byte[] bytes=(byte[])Dao.company_image().getData();
			//System.out.println(bytes.length);
			response.setContentLength(bytes.length);
			System.out.println(bytes.length);
			OutputStream outputStream=response.getOutputStream();
			outputStream.write(bytes);
			outputStream.flush();
			return;
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
