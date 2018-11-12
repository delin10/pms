package pms.service.impl;

import pms.service.BasicService;

public class EmployeeService extends BasicService{

	@Override
	public String getTable() {
		// TODO Auto-generated method stub
		return "employee";
	}

	@Override
	public String getCommunityColumn() {
		// TODO Auto-generated method stub
		return "community_name";
	}

}
