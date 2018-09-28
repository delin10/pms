package pms.util.auth;

import pms.util.auth.bean.Resource;
import pms.util.auth.bean.Role;
import pms.util.auth.manager.ResourceManager;
import pms.util.auth.manager.RoleManager;
import pms.util.auth.manager.UserManager;
import pms.util.auth.proxy.impl.BasicProxy;
import pms.util.redis.cahce.Cache;
import pms.util.redis.cahce.Cache.CacheSwap;

public class Getter {
	public static Cache cache = new Cache();
	public static UserManager um;
	public static ResourceManager am;
	public static RoleManager rolem;
	static {
		cache();
		um = new UserManager(new BasicProxy());
		am = new ResourceManager();
		rolem = new RoleManager();
	}

	private static void cache() {
		CacheSwap swap = new CacheSwap("roles", "id", Role.class);
		swap.setMAX_ID(true);
		cache.register(swap);
		swap = new CacheSwap("resources", "id", Resource.class);
		cache.register(swap);
	}
}
