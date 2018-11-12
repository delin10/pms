package pms.service.impl;

import java.util.Arrays;
import java.util.Map;

import pms.comm.RuntimeStorage;
import pms.service.BasicService;
import pms.util.auth.bean.Role;
import pms.util.auth.manager.Session;
import pms.util.comm.Info;
import pms.util.comm.bean.BeanUtil;

public class RoleService extends BasicService {
	@Override
	public Info query(Session session) {
		return RuntimeStorage.getGetter().getRoleManager().roles();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Info add(Session session, Object values) {
		return RuntimeStorage.getGetter().getRoleManager()
				.addRole((Role) BeanUtil.mapToBean((Map<String, String>) values, Role.class));
	}

	@Override
	@SuppressWarnings("unchecked")
	public Info update(Session session, Object values, Map<String, String> keys) {
		System.out.println(values);
		return RuntimeStorage.getGetter().getRoleManager()
				.updateRole((Role) BeanUtil.mapToBean((Map<String, String>) values, Role.class));
	}

	@Override
	@SuppressWarnings("unchecked")
	public Info delete(Session session, Object keys) {
		Info info = new Info();
		Arrays.stream((Map<String, String>[]) keys).forEach(map -> {
			RuntimeStorage.getGetter().getRoleManager().delRole(map.get("id"));
		});
		info.suc("success");
		return info;
	}

	@Override
	public String getTable() {
		// TODO Auto-generated method stub
		return "role";
	}

	@Override
	public String getCommunityColumn() {
		// TODO Auto-generated method stub
		return null;
	}

}
