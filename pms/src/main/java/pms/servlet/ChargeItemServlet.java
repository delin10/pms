package pms.servlet;

import pms.comm.RuntimeStorage;
import pms.service.BasicService;

public class ChargeItemServlet extends BasicServiceServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public BasicService getService() {
		// TODO Auto-generated method stub
		return RuntimeStorage.getChargeItemService();
	}


}
