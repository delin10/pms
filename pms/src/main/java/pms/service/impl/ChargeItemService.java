package pms.service.impl;

import pms.service.BasicService;

public class ChargeItemService extends BasicService {

	@Override
	public String getTable() {
		// TODO Auto-generated method stub
		return "charge_item";
	}

	@Override
	public String getCommunityColumn() {
		// TODO Auto-generated method stub
		return "community_name";
	}

}
