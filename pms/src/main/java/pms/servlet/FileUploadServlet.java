package pms.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import pms.comm.DataWrapper;
import pms.service.FileService;
import pms.util.comm.Info;

/**
 * Servlet implementation class FileUploadServlet
 */
public class FileUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FileUploadServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		DataWrapper res = new DataWrapper();
		res.fail("not support service");
		response.getWriter().write(JSON.toJSONString(res));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("file uploader ---------------------------");
		DataWrapper res = new DataWrapper();
		byte[] bytes = (byte[]) request.getAttribute("body");
		res.fail("解析文件失败");
		if (bytes != null) {
			System.out.println("上传的文件长度:" + bytes.length);
			FileService service = new FileService();
			Info info = service.fileUpload(bytes);
			res.wrapInfo(info);
		}
		response.getWriter().write(JSON.toJSONString(res));
	}

}
