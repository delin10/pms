package pms.service.impl;

import pms.service.BasicService;

public class ChargeFormService extends BasicService {

	@Override
	public String getTable() {
		// TODO Auto-generated method stub
		return "charge_form";
	}

	@Override
	public String getCommunityColumn() {
		// TODO Auto-generated method stub
		return "community_name";
	}

}
