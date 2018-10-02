package pms.util.auth.manager;

import pms.util.auth.Getter;
import pms.util.auth.bean.Role;
import pms.util.comm.Info;
import pms.util.db.DBUtil;
import pms.util.db.DBUtil.KV;
import pms.util.db.DBUtil.Keys;
import pms.util.db.DBUtil.SQL;
import pms.util.redis.cahce.Cache;

public class RoleManager {
	private String rel_table="roles";
	public final String SYS="0";
	public final String NORMAL="1";
	private static Cache cache=new Cache();
	public Info roles() {
		Info info=new Info();
		info.setData(cache.get("roles").all());
		info.suc("success");
		return info;
	}
	public Info auth(Session session, String user_id, String role_id) {
		Info info = new Info();
		// 需要判断管理员权限
		//System.out.println(session.getAttributes("role"));
		Role role = (Role) cache.get("roles").get(session.getAttributes("role").toString());
		if ("0".equals(role.getId()) && !"0".equals(user_id) && !"0".equals(role_id)) {
			boolean res=DBUtil.transaction()
			.begin()
			.add(SQL.create().delete("users_roles").where(new Keys().start(new KV("user_id",user_id))))
			.add(SQL.create().insert("users_roles").values(new KV("user_id",user_id),new KV("role_id",role_id)))
			.commit();
			if (res) {
				Getter.sm.getSessionOf(user_id).setAttributes("role", role_id);
				info.suc("成功更换角色");
			}else {
				info.fail("更换角色失败[可能原因:数据库事务发生回滚]");
			}
		}else {
			info.fail("权限不足或者尝试授予系统管理员权限");
		}
		return info;
	}
	
	
	public Info addRole(Role role) {
		Info info=new Info();
		if(cache.get("roles").updateCache(role)) {
			info.suc("成功添加角色");
		}else {
			info.fail("添加角色失败");
		}
		return info;
	}
	
	public Info delRole(String role_id) {
		Info info=new Info();
		if(cache.get(rel_table).deleteCache(role_id)) {
			info.suc("成功删除");
		}else {
			info.fail("删除失败");
		}
		return info;	
	}
	
	/**
	 * 获取角色对象
	 * @param role_id
	 * @return 信息对象
	 */
	public Info getRole(String role_id) {
		Info info=new Info();
		Object o=cache.get("roles").get(role_id);
		if (o==null) {
			info.fail("not founf!");
		}else {
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
		if (sys==null) {
			Role role=new Role();
			role.setId(SYS);
			role.setDescription("系统管理员");
			role.setAvailable("1");
			role.setName("sys");
			cache.get(rel_table).updateCache(role);
		}
		
		if (normal==null) {
			Role role=new Role();
			role.setId(NORMAL);
			role.setDescription("普通用户");
			role.setAvailable("1");
			role.setName("normal");
			cache.get(rel_table).updateCache(role);
		}
	}
	
}
