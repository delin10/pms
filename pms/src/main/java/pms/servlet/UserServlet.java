package pms.servlet;

import pms.comm.RuntimeStorage;
import pms.service.BasicService;

public class UserServlet extends BasicServiceServlet {
	private static final long serialVersionUID = 6404771369090815973L;

	@Override
	public BasicService getService() {
		// TODO Auto-generated method stub
		return RuntimeStorage.getUserService();
	}

}
