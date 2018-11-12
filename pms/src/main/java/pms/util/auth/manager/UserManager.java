package pms.util.auth.manager;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import pms.dao.Dao;
import pms.util.Encrypter;
import pms.util.auth.Getter;
import pms.util.auth.bean.Resource;
import pms.util.auth.bean.Role;
import pms.util.auth.bean.User;
import pms.util.auth.proxy.Proxy;
import pms.util.auth.proxy.impl.BasicProxy;
import pms.util.comm.Info;
import pms.util.comm.KV_Obj;
import pms.util.comm.lambda.StreamUtil;
import pms.util.comm.lambda.exception.Handler;
import pms.util.comm.lambda.exception.SimpleExec;
import pms.util.comm.lambda.functon.SimpleTask;
import pms.util.db.DBUtil;
import pms.util.db.DBUtil.KV;
import pms.util.db.DBUtil.SQL;
import pms.util.redis.cahce.Cache.CacheSwap;
import pms.util.redis.driver.RedisDriver;
import pms.util.redis.driver.RedisDriver.DriverConfig;
import pms.util.reflect.Reflector;

public class UserManager {
	private static final String ADMIN = "admin";
	private RedisDriver driver = new RedisDriver();
	private Proxy proxy;

	{
		if (!driver.ready()) {
			DriverConfig conf = new DriverConfig();
			try {
				conf.load("pms/conf/redis.props");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// driver = new RedisDriver();
			driver.getInstance(conf);
		}
	}

	public UserManager(Proxy proxy) {
		this.proxy = proxy;
	}

	public Info login(String id, String pwd, Map<String, Object> attrs, SimpleTask task) {
		Info info = new Info();
		Object o = proxy.verify(id, pwd, attrs);
		info.fail("failed");
		if (o != null) {
			SimpleExec.exec((data) -> {
				long timestamp = Instant.now().getEpochSecond();
				String session_key = Encrypter.Md5(id + ":" + pwd + ":" + timestamp);
				Session session = new Session(session_key, o);
				init(session);
				if (task != null) {
					session.setAttributes("_basic_",task.exec(new Object[] { session }));
				}
				Getter.sm.addSession(session);
				info.setData(session);
				return session;
			}, Handler.PRINTTRACE);
			info.suc("success");
		}
		return info;
	}

	public Session check(String session_id) {
		return (Session) SimpleExec.exec((data) -> {
			Session session = Getter.sm.getSession(session_id);
			if (session != null) {
				Getter.sm.refresh(session_id);
			}
			return session;
		}, Handler.CARELESS);
	}

	/**
	 * 添加用戶 管理员默认权限为0 普通用户添加为1
	 * 
	 * @param user
	 */
	public void add(User user) {
		user.setPwd(proxy.encrypt_pwd(user.getPwd(), null));
		DBUtil.transaction().add(SQL.create().insertObject("users", user))
				.add(SQL.create().insert("users_roles").values(new KV("user_id", user.getId()),
						new KV("role_id", user.getId().equals(ADMIN) ? RoleManager.SYS : RoleManager.NORMAL)))
				.commit();
		// System.out.println(res);
	}

	public void init(Session session) {
		Role role = roleOf(((User) session.getAttributes("user")).getId());
		ArrayList<Resource> resources = resources(role.getId());
		session.setAttributes("role", role.getId());
		session.setAttributes("resources", resources);
		// HashMap<String, Object> map=new HashMap<>();
		// map.put("resources", resources);
		// map.put("role", role);
		// return map;
	}

	public void init() {
		// System.out.println(Getter.cache.get("users").get(ADMIN));
		CacheSwap swap = Getter.cache.get("users");
		if (swap.get(ADMIN) == null) {
			User user = new User();
			user.setId(ADMIN);
			user.setPwd("123456");
			user.setRel_id("");
			add(user);
			swap.cacheAll();
		}
		swap.sync();
	}

	public Info getPermission(Session session, String resource_id) {
		Info info = new Info();
		if (session.getAttributes("role").equals(RoleManager.SYS)) {
			info.suc("permission");
		} else {
			@SuppressWarnings("unchecked")
			ArrayList<Resource> resources = (ArrayList<Resource>) session.getAttributes("resources");
			HashSet<Resource> set = StreamUtil.find(resources, resource_id, Resource::getId);
			if (set.size() == 0) {
				info.fail("no permission");
			} else {
				info.suc("permission");
			}
			info.setData(set);
		}
		return info;
	}

	public Role roleOf(String user_id) {
		String role_id = DBUtil.toArrayListOf("role_id", DBUtil.keyQuery("users_roles", new KV("user_id", user_id)))
				.get(0);
		// System.out.println("ROLW" + role_id);
		return (Role) Getter.rolem.getRole(role_id).getData();
	}

	public ArrayList<Resource> resources(String role_id) {
		ArrayList<String> list;
		if (role_id.equals(RoleManager.SYS)) {
			list = DBUtil.toArrayListOf("id", DBUtil.queryAll("resources", null));
			// System.out.println(list.size());
		} else {
			list = DBUtil.toArrayListOf("resource_id", DBUtil.keyQuery("roles_resources", new KV("role_id", role_id)));
		}

		return list.stream().map(Getter.am::getResources).collect(Collectors.toCollection(ArrayList<Resource>::new));
	}

	/**
	 * 过滤资源
	 * 
	 * @param role_id
	 * @param kvs
	 * @return
	 */
	public ArrayList<Resource> resources_where(String role_id, KV_Obj... kvs) {
		ArrayList<String> list;
		if (role_id.equals(RoleManager.SYS)) {
			list = DBUtil.toArrayListOf("id", DBUtil.queryAll("resources", null));
			// System.out.println(list.size());
		} else {
			list = DBUtil.toArrayListOf("resource_id", DBUtil.keyQuery("roles_resources", new KV("role_id", role_id)));
		}
		return list.stream().map(Getter.am::getResources).filter(resource -> {
			return (boolean) Arrays.stream(kvs).map(kv -> {
				return (boolean) SimpleExec.exec(data -> {
					Object value = Reflector.get(resource, kv.getAttr());
					System.out.println("执行资源过滤:" + resource+"="+value);
					return kv.compare(value) == 0;
				}, Handler.PRINTTRACE);

			}).reduce((acc, next) -> acc && next).get();
		}).collect(Collectors.toCollection(ArrayList<Resource>::new));

	}

	public void updateSession(Session session) {
		driver.set("session", session.getSession_id(), session);
	}

	public ArrayList<Object> users() {
		return DBUtil.toArrayList(DBUtil.queryAll("users", null), User.class);
	}

	public void forbidden() {

	}

	public void changePwd() {

	}

	public void destroy() {
		driver.keys("session*").forEach(driver::remove);
		driver.keys("timestamp*").forEach(driver::remove);
	}
}
