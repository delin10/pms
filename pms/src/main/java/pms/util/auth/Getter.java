package pms.util.auth;

import pms.util.auth.bean.Resource;
import pms.util.auth.bean.Role;
import pms.util.auth.bean.User;
import pms.util.auth.manager.ResourceManager;
import pms.util.auth.manager.RoleManager;
import pms.util.auth.manager.SessionManager;
import pms.util.auth.manager.UserManager;
import pms.util.auth.proxy.impl.BasicProxy;
import pms.util.db.DBUtil.KV;
import pms.util.db.DBUtil.Keys;
import pms.util.db.DBUtil.SQL;
import pms.util.redis.cahce.Cache;

public class Getter {
	public static Cache cache = new Cache();
	public static UserManager um;
	public static ResourceManager am;
	public static SessionManager sm;
	public static RoleManager rolem;
	{
		cache();
		um = new UserManager(new BasicProxy());
		am = new ResourceManager();
		rolem = new RoleManager();
		sm = new SessionManager();
		rolem.init();
		um.init();
	}

	private void cache() {
		Cache.CacheSwap swap = cache.new CacheSwap("roles", "id", Role.class);
		swap.setMAX_ID(true);
		cache.register(swap);
		swap = cache.new CacheSwap("resources", "id", Resource.class);
		cache.register(swap);
		swap = cache.new CacheSwap("users", "id", User.class);
		swap.setCache_sql(SQL.create()
				.select(new KV("users", "id").unwrap(), new KV("users", "pwd").unwrap(),
						new KV("users", "rel_id").unwrap())
				.from("users").where(new Keys().start(new KV("id", RoleManager.SYS))).complete());
		swap.setMAX_ID(true);
		cache.register(swap);
	}

	public UserManager getUserManager() {
		return um;
	}

	public ResourceManager getResourceManager() {
		return am;
	}

	public SessionManager getSessionManager() {
		return sm;
	}

	public RoleManager getRoleManager() {
		return rolem;
	}

	public void setUserManager(UserManager um) {
		this.um = um;
	}

	public void setResourceManager(ResourceManager am) {
		this.am = am;
	}

	public void setSessionManager(SessionManager sm) {
		this.sm = sm;
	}

	public void setRoleManager(RoleManager rolem) {
		this.rolem = rolem;
	}
}
