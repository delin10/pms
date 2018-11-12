package pms.util.auth.manager;

import java.util.ArrayList;

import pms.util.auth.Getter;
import pms.util.auth.bean.Role;
import pms.util.comm.Info;
import pms.util.db.DBUtil;
import pms.util.db.DBUtil.KV;
import pms.util.db.DBUtil.Keys;
import pms.util.db.DBUtil.SQL;
import pms.util.redis.cahce.Cache;
import pms.util.redis.cahce.Cache.CacheSwap;

public class RoleManager {
	public static String rel_table = "roles";
	public static final String SYS = "0";
	public static final String NORMAL = "1";
	private static Cache cache = new Cache();

	public Info roles() {
		Info info = new Info();
		info.setData(cache.get("roles").all());
		info.suc("success");
		return info;
	}

	public Info auth(String user_id, String role_id) {
		Info info = new Info();
		if (!SYS.equals(user_id) && !SYS.equals(role_id)) {
			boolean res = DBUtil.transaction().begin()
					.add(SQL.create().delete("users_roles").where(new Keys().start(new KV("user_id", user_id))))
					.add(SQL.create().insert("users_roles").values(new KV("user_id", user_id),
							new KV("role_id", role_id)))
					.commit();
			if (res) {
				Session session = Getter.sm.getSessionOf(user_id);
				if (session != null) {
					session.setAttributes("role", role_id);
				}
				info.suc("成功更换角色");
			} else {
				info.fail("更换角色失败[可能原因:数据库事务发生回滚]");
			}
		} else {
			info.fail("权限不足或者尝试授予系统管理员权限");
		}
		return info;
	}

	public Info updateRole(Role role) {
		Info info = new Info();
		boolean res = cache.get("roles").updateCache(role);
		if (res) {
			info.suc("成功更新");
		} else {
			info.fail("更新失败");
		}
		return info;
	}

	public Info delRole(String role_id) {
		Info info = new Info();
		if (SYS.equals(role_id) || NORMAL.equals(role_id)) {
			info.fail("尝试删除内定角色-[管理员、普通用户]");
			;
		} else {
			ArrayList<String> user_ids = DBUtil.toArrayListOf("user_id",
					DBUtil.keyQuery("users_roles", new KV("role_id", role_id)));
			if (user_ids.size() == 0) {
				info.suc("该角色无关联用户");
				;
			} else {
				user_ids.forEach(user_id -> {
					info.wrapInfo(auth(user_id, role_id).append(String.format(",该角色关联%s个用户", user_ids.size())));
				});
			}

			info.line();
			boolean res = DBUtil.keyDel("roles_resources", new KV("role_id", role_id));
			if (res) {
				info.append("成功删除关联资源");
			} else {
				info.append("删除角色关联资源发生异常");
			}
			boolean del_role_res = cache.get("roles").deleteCache(role_id);
			info.line();
			if (del_role_res) {
				info.append("成功删除角色");
			} else {
				info.append("删除角色异常");
			}
		}
		return info;
	}

	public Info addRole(Role role) {
		Info info = new Info();
		if (cache.get("roles").updateCache(role)) {
			info.suc("成功添加角色");
		} else {
			info.fail("添加角色失败");
		}
		return info;
	}

	/**
	 * 获取角色对象
	 * 
	 * @param role_id
	 * @return 信息对象
	 */
	public Info getRole(String role_id) {
		Info info = new Info();
		Object o = cache.get("roles").get(role_id);
		if (o == null) {
			info.fail("not founf!");
		} else {
			info.suc("found!");
			info.setData(o);
		}
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
		if (SYS.equals(role.getId())) {
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
	 * 
	 * @param role_id
	 * @return
	 */
	public Info permit(Role role) {
		Info info = new Info();
		if (SYS.equals(role.getId())) {
			info.fail("dont be allowed to forbidden sys Manager");
			return info;
		}

		role.setAvailable("1");
		cache.get("roles").updateCache(role);
		info.suc("succeed to permit!");
		return info;
	}

	public void init() {
		Object sys = cache.get(rel_table).get(SYS);
		Object normal = cache.get(rel_table).get(NORMAL);
		CacheSwap swap = cache.get(rel_table);
		if (sys == null) {
			Role role = new Role();
			role.setId(SYS);
			role.setDescription("系统管理员");
			role.setAvailable("1");
			role.setName("sys");
			swap.updateCache(role);
		}

		if (normal == null) {
			Role role = new Role();
			role.setId(NORMAL);
			role.setDescription("普通用户");
			role.setAvailable("1");
			role.setName("normal");
			swap.updateCache(role);
		}
		swap.sync();
	}

	public String generateId() {
		return cache.get("roles").incMaxId();
	}

}
