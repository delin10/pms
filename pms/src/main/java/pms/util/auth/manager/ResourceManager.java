package pms.util.auth.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

import pms.util.array.CollectionUtil;
import pms.util.auth.Getter;
import pms.util.auth.bean.Resource;
import pms.util.comm.Info;
import pms.util.db.DBUtil;
import pms.util.db.DBUtil.KV;
import pms.util.db.DBUtil.Keys;
import pms.util.db.DBUtil.SQL;
import pms.util.db.DBUtil.Transaction;
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
	public Info auth(Session session, String resource, String role_id) {
		Info info = new Info();
		Resource resource_ = (Resource) cache.get("resources").get(resource);
		ArrayList<String> list = new ArrayList<>();
		ArrayList<Resource> resources = Getter.um.resources(role_id);
		while (resource_ != null) {
			if (!resource.contains(resource)) {
				resources.add(resource_);
				list.add(SQL.create().insert("roles_resources")
						.values(new KV("role_id", role_id), new KV("resource_id", resource_.getId())).complete());
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
	 * 授权
	 * 
	 * @param role_id
	 * @param resource_id
	 * @return
	 */
	public Info auth(Session session, ArrayList<String> resource_ids, String role_id) {
		Info info = new Info();
		if (Getter.rolem.SYS.equals(role_id)) {
			info.fail("尝试修改管理员的访问权限，遭到阻止");
		} else {
			ArrayList<Resource> resources_pass = resource_ids.stream().map(Getter.am::getResources)
					.collect(Collectors.toCollection(ArrayList<Resource>::new));
			ArrayList<String> list = new ArrayList<>();
			ArrayList<Resource> resources = Getter.um.resources(role_id);
			//如果涉及到HashSet，必须实现hashCode方法
			ArrayList<Resource> diff_resources = (ArrayList<Resource>) CollectionUtil.diff(resources, resources_pass);
			Iterator<Resource> iterator = resources_pass.iterator();
			while (iterator.hasNext()) {
				Resource resource_ = iterator.next();
				while (resource_ != null) {
					if (!resources.contains(resource_)) {
						resources.add(resource_);
						list.add(SQL.create().insert("roles_resources")
								.values(new KV("role_id", role_id), new KV("resource_id", resource_.getId()))
								.complete());
					}
					resource_ = (Resource) cache.get("resources").get(resource_.getFid());
				}
			}
			session.setAttributes("resources", resources_pass);
			//System.out.println("传递的");
			//resource_ids.forEach(System.out::println);
			//System.out.println("差集的");
			//diff_resources.stream().map(Resource::getId).forEach(System.out::println);
			//System.out.println("本来的");
			//resources.stream().map(Resource::getId).forEach(System.out::println);

			diff_resources.stream()
					.map(resource -> SQL.create().delete("roles_resources").where(
							new Keys().start(new KV("role_id", role_id)).and(new KV("resource_id", resource.getId())))
							.complete())
					.forEach(list::add);
			boolean res = DBUtil.transaction().begin().add(list).commit();
			if (res) {
				Getter.sm.addSession(session);
				info.suc("success");
			} else {
				info.fail("fail");
			}
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
	public Info dropAuth(String role_id, ArrayList<Resource> resources) {
		Info info = new Info();
		Transaction transaction = DBUtil.transaction();

		transaction.begin();
		boolean res = transaction.commit();
		if (res) {
			info.setInfo("fail");
		} else {
			info.setInfo("success");
		}
		// cache.get("roles_resources").deleteCache(rrid);
		return info;
	}

	public Resource getResources(String resource_id) {
		return (Resource) cache.get("resources").get(resource_id);
	}

	public ArrayList<Object> all() {
		return cache.get("resources").all();
	}
	/**
	 * 进行资源和角色的缓存
	 */

}
