package pms.util.auth.manager;

import java.util.ArrayList;

import pms.util.auth.bean.Resource;
import pms.util.comm.Info;
import pms.util.db.DBUtil;
import pms.util.db.DBUtil.KV;
import pms.util.db.DBUtil.SQL;
import pms.util.redis.cahce.Cache;

public class ResourceManager {
	private Cache cache = new Cache();

	/**
	 * 授权
	 * 
	 * @param role_id
	 * @param resource_id
	 * @return
	 */
	public Info auth(Session session, String resource) {
		Info info = new Info();
		Resource resource_ = (Resource) cache.get("resources").get(resource);
		ArrayList<String> list = new ArrayList<>();
		@SuppressWarnings("unchecked")
		ArrayList<Resource> resources = (ArrayList<Resource>) session.getAttributes("resources");
		while (resource_ != null) {
			list.add(SQL.create().insert("roles_resources")
					.values(new KV("role_id", session.getAttributes("role")), new KV("resource_id", resource_.getId()))
					.complete());
			if (!resource.contains(resource)) {
				resources.add(resource_);
			}
			resource_ = (Resource) cache.get("resources").get(resource_.getFid());
		}
		session.setAttributes("resources", resources);

		boolean res = DBUtil.transaction().begin().add(list).commit();
		if (res) {
			info.suc("success");
		} else {
			info.fail("fail");
		}
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

	public Resource getResources(String resource_id) {
		return (Resource) cache.get("resources").get(resource_id);
	}
	/**
	 * 进行资源和角色的缓存
	 */

}
