package pms.util.auth.manager;

import java.time.Instant;
import java.util.Map;

import pms.util.Encrypter;
import pms.util.auth.bean.Role;
import pms.util.auth.bean.User;
import pms.util.auth.proxy.Proxy;
import pms.util.comm.lambda.exception.Handler;
import pms.util.comm.lambda.exception.SimpleExec;
import pms.util.redis.driver.RedisDriver;

public class UserManager {
	private RedisDriver driver = new RedisDriver();
	public Object login(String id,String pwd,Map<String,Object> attrs,Proxy proxy) {
		Object o=proxy.verify(id,pwd,attrs);
		if (o!=null) {
			return SimpleExec.exec(()->{
				long timestamp=Instant.now().getEpochSecond();
				String timestamp_key=Encrypter.Md5(id+":"+pwd);
				String session_key=Encrypter.Md5(id+":"+pwd+":"+timestamp);
				Session session=new Session(session_key,o);
				String skey=driver.combine("session", session_key);
				String tkey=driver.combine("timestamp", timestamp_key);
				driver.set(skey, session);
				driver.expire(skey, session.getEXPIRE_TIME());
				driver.set(tkey, timestamp);
				driver.expire(tkey, session.getEXPIRE_TIME());
				return session_key;
			}, Handler.CARELESS);
		}
		return null;
	}
	
	public Session check(String session_id) {
		return (Session) SimpleExec.exec(()->{
			Session session= (Session) driver.get("session",session_id);
			if (session!=null) {
				driver.expire("session", session_id, session.getEXPIRE_TIME());
			}
			return session;
		}, Handler.CARELESS);
	}
	
	public void forbidden() {
		
	}
	
	public void auth(User user,Role role) {
		
	}
	
	public void changePwd() {
		
	}
}
