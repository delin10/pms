package pms.util.auth.manager;

import pms.util.auth.bean.Role;
import pms.util.auth.bean.Roles_Resources;
import pms.util.comm.Info;
import pms.util.redis.cahce.Cache;

public class AuthManager {
	private static Cache cache=new Cache();
	private static String sys;

	public AuthManager(String sys) {
		AuthManager.sys=sys;
	}
	/**
	 * 添加角色
	 * 
	 * @param name
	 * @param description
	 * @param available
	 * @return
	 */
	public Info updateRole(Role role) {
		Info info = new Info();
		cache.get("roles").updateCache(role);;
		return info;
	}

	/**
	 * 删除角色
	 * 
	 * @param role_id
	 *            角色主键id
	 * @return
	 */
	public Info delRole(Role role) {
		Info info = new Info();
		cache.get("roles").deleteCache(role);;
		return info;
	}

	/**
	 * 授权
	 * 
	 * @param role_id
	 * @param resource_id
	 * @return
	 */
	public Info auth(String role, String resource) {
		Info info = new Info();
		Roles_Resources rr=new Roles_Resources();
		rr.setRole_id(role);
		rr.setResource_id(resource);
		cache.get("roles_resource").updateCache(rr);
		return info;
	}

	/**
	 * 判断系统管理员
	 * 
	 * @param role_id
	 * @return
	 */
	public Info isSystem(String role_id) {
		Info info = new Info();
		info.setStatus(sys.equals(role_id)?0:-1);
		return info;
	}

	/**
	 * 禁用某个绝色
	 * 
	 * @param role_id
	 * @return
	 */
	public Info forbidden(Role role) {
		Info info = new Info();
		if (sys.equals(role.getId())) {
			info.fail("dont be allowed to forbidden sys Manager");
			return info;
		}
		
		role.setAvailable("0");
		cache.get("roles").updateCache(role);
		info.suc("succeed to forbidden!");
		return info;
	}
	/**
	 * 允许角色
	 * @param role_id
	 * @return
	 */
	public Info permit(Role role) {
		Info info = new Info();
		if (sys.equals(role.getId())) {
			info.fail("dont be allowed to forbidden sys Manager");
			return info;
		}
		
		role.setAvailable("1");
		cache.get("roles").updateCache(role);
		info.suc("succeed to permit!");
		return info;
	}
	/**
	 * 撤销权限
	 * @param role_id
	 * @param resource_id
	 * @return
	 */
	public Info dropAuth(Roles_Resources rr) {
		Info info=new Info();
		cache.get("roles_resources").deleteCache(rr);
		return info;
	}
}
