package pms.util.auth.manager;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import pms.util.Encrypter;
import pms.util.auth.Getter;
import pms.util.auth.bean.Resource;
import pms.util.auth.bean.Role;
import pms.util.auth.bean.User;
import pms.util.auth.proxy.Proxy;
import pms.util.auth.proxy.impl.BasicProxy;
import pms.util.comm.Info;
import pms.util.comm.lambda.StreamUtil;
import pms.util.comm.lambda.exception.Handler;
import pms.util.comm.lambda.exception.SimpleExec;
import pms.util.db.DBUtil;
import pms.util.db.DBUtil.KV;
import pms.util.db.DBUtil.SQL;
import pms.util.redis.driver.RedisDriver;
import pms.util.redis.driver.RedisDriver.DriverConfig;

public class UserManager {
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
			RedisDriver.getInstance(conf);
		}
	}

	public UserManager(Proxy proxy) {
		this.proxy = proxy;
	}

	public Info login(String id, String pwd, Map<String, Object> attrs) {
		Info info = new Info();
		Object o = proxy.verify(id, pwd, attrs);
		info.fail("failed");
		if (o != null) {
			info.suc("success");
			info.setData(SimpleExec.exec((data) -> {
				long timestamp = Instant.now().getEpochSecond();
				String timestamp_key = Encrypter.Md5(id + ":" + pwd);
				String session_key = Encrypter.Md5(id + ":" + pwd + ":" + timestamp);
				Session session = new Session(session_key, o);
				init(session);
				String skey = driver.combine("session", session_key);
				String tkey = driver.combine("timestamp", timestamp_key);
				driver.set(skey, session);
				driver.expire(skey, session.getEXPIRE_TIME());
				driver.set(tkey, timestamp);
				driver.expire(tkey, session.getEXPIRE_TIME());
				return session_key;
			}, Handler.PRINTTRACE));
		}
		return info;
	}

	public Session check(String session_key) {
		return (Session) SimpleExec.exec((data) -> {
			Session session = (Session) driver.get("session", session_key);
			if (session != null) {
				driver.expire("session", session_key, session.getEXPIRE_TIME());
			}
			return session;
		}, Handler.CARELESS);
	}

	public void add(User user) {
		user.setPwd(proxy.encrypt_pwd(user.getPwd(), null));
		DBUtil.transaction().add(SQL.create().insertObject("users", user))
				.add(SQL.create().insert("users_roles").values(new KV("user_id", user.getId()), new KV("role_id", 1)))
				.commit();
		// System.out.println(res);
	}

	public void init(Session session) {
		Role role = roleOf(((User) session.getAttributes("user")).getId());
		session.setAttributes("role", role.getId());
		session.setAttributes("resources", resources(role.getId()));
	}

	public Info getPermission(Session session, String resource_id) {
		Info info = new Info();
		@SuppressWarnings("unchecked")
		ArrayList<Resource> resources = (ArrayList<Resource>) session.getAttributes("resources");
		HashSet<Resource> set = StreamUtil.find(resources, resource_id, Resource::getId);
		if (set.size() == 0) {
			info.fail("no permission");
		} else {
			info.suc("permission");
		}
		info.setData(set);
		return info;
	}

	public Role roleOf(String user_id) {
		String role_id = DBUtil.toArrayListOf("role_id", DBUtil.keyQuery("users_roles", new KV("user_id", user_id)))
				.get(0);
		return (Role) Getter.rolem.getRole(role_id).getData();
	}

	public ArrayList<Resource> resources(String role_id) {
		ArrayList<String> list = DBUtil.toArrayListOf("role_id",
				DBUtil.keyQuery("roles_resources", new KV("role_id", role_id)));
		return list.stream().map(Getter.am::getResources).collect(Collectors.toCollection(ArrayList<Resource>::new));
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

	//
	public static void main(String... args) throws Exception {
		DBUtil.init();
		/*int id = 0;
		UserManager manager = new UserManager(new BasicProxy());

		while (id++ < 100) {
			User user = new User();
			user.setId("" + id);
			user.setOwner_id("123131" + id);
			user.setPwd("123");
			manager.add(user);
			// Thread.sleep(10);
		}*/

		/*
		 * Session session=manager.check(manager.login("1", "123", null).toString());
		 * System.out.println(session.getAttributes("resources"));
		 */
		ArrayList<String> list=new ArrayList<String>();
		list.add("1");
		String json=JSON.toJSONString(list);
		System.out.println(json);
		list=JSON.parseObject(json, ArrayList.class);
		System.out.println(list.size());
	}
}
