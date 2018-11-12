package pms.service.impl;

import pms.service.BasicService;

public class OwnerService extends BasicService {

	@Override
	public String getTable() {
		// TODO Auto-generated method stub
		return "owner";
	}

	@Override
	public String getCommunityColumn() {
		// TODO Auto-generated method stub
		return "commnunity_name";
	}

}
