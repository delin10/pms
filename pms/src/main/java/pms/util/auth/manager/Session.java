package pms.util.auth.manager;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Session {
	private String session_id;
	private long crttime;
	private int EXPIRE_TIME=60;
	private Map<String,Object> attributes=new HashMap<>();
	
	public Session(String session_id,Object o) {
		this.session_id=session_id;
		attributes.put("user", o);
		crttime=Instant.now().getEpochSecond();
	}
	public String getSession_id() {
		return session_id;
	}
	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}
	public long getCrttime() {
		return crttime;
	}
	public int getEXPIRE_TIME() {
		return EXPIRE_TIME;
	}
	public void setEXPIRE_TIME(int eXPIRE_TIME) {
		EXPIRE_TIME = eXPIRE_TIME;
	}
	public Object getAttributes(String name) {
		return attributes;
	}
	public void setAttributes(String name,Object value) {
		if ("user".equals(name)) {
			return;
		}
		this.attributes.put(name, value);
	}
}
