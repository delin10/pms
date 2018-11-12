package pms.service.impl;

import pms.service.BasicService;
import pms.util.auth.manager.Session;
import pms.util.db.DBUtil.Keys;

public class ContractService extends BasicService {

	@Override
	public String getTable() {
		// TODO Auto-generated method stub
		return "contract";
	}
	
	@Override
	public Keys keys(Session session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCommunityColumn() {
		// TODO Auto-generated method stub
		return null;
	}

}
