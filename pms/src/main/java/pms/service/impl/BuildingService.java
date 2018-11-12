package pms.service.impl;

import pms.service.BasicService;

public class BuildingService extends BasicService {

	@Override
	public String getTable() {
		// TODO Auto-generated method stub
		return "building";
	}

	@Override
	public String getCommunityColumn() {
		// TODO Auto-generated method stub
		return "community_name";
	}

}
