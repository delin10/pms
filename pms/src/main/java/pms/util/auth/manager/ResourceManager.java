package pms.util.auth.manager;

import pms.util.auth.bean.Resource;
import pms.util.auth.bean.Roles_Resources;
import pms.util.comm.Info;
import pms.util.redis.cahce.Cache;

public class ResourceManager {
	private static Cache cache = new Cache();

	/**
	 * 授权
	 * 
	 * @param role_id
	 * @param resource_id
	 * @return
	 */
	public Info auth(String role, String resource) {
		Info info = new Info();
		Roles_Resources rr = new Roles_Resources();
		rr.setRole_id(role);
		rr.setResource_id(resource);
		cache.get("roles_resource").updateCache(rr);
		return info;
	}



	/**
	 * 撤销权限
	 * 
	 * @param role_id
	 * @param resource_id
	 * @return
	 */
	public Info dropAuth(String rrid) {
		Info info = new Info();
		cache.get("roles_resources").deleteCache(rrid);
		return info;
	}

	
	public Resource getResources(String resource_id){
		return (Resource) cache.get("resources").get(resource_id);
	}
	/**
	 * 进行资源和角色的缓存
	 */

}
