package pms.util.auth.manager;

import java.io.IOException;

import pms.util.auth.bean.User;
import pms.util.redis.driver.RedisDriver;
import pms.util.redis.driver.RedisDriver.DriverConfig;

public class SessionManager {
	private static String table = "session";
	private static String user_session = "userid-session";
	private static int EXPIRETIME = 1000;
	private RedisDriver driver = new RedisDriver();
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

	public void addSession(Session session) {
		driver.set(table, session.getSession_id(), session);
		driver.set(user_session, ((User) session.getAttributes("user")).getId(), session.getSession_id());
		refresh(session);
	}
	

	public Session getSession(String session_id) {
		return (Session) driver.get(table, session_id);
	}
	
	public String getSession_id(String user_id) {
		return (String) driver.get(user_session, user_id);
	}

	private void refresh(Session session) {
		refresh(session.getSession_id());
		driver.expire(user_session, ((User) session.getAttributes("user")).getId(), EXPIRETIME);
	}

	public void refresh(String session_id) {
		driver.expire(table, session_id, EXPIRETIME);
		Session session=getSession(session_id);
		driver.expire(user_session, ((User) session.getAttributes("user")).getId(), EXPIRETIME);
	}

	public void expired(String session_id) {
		Session session=getSession(session_id);
		driver.remove(user_session, ((User) session.getAttributes("user")).getId());
		driver.remove(table, session_id);
	}
	
	Session getSessionOf(String user_id) {
		return getSession(getSession_id(user_id));
	}

	public void destroy() {
		driver.keys(table + "*").stream().forEach(driver::remove);
		driver.keys(user_session + "*").stream().forEach(driver::remove);
	}
}
