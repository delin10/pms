package pms.util.auth.proxy;

import java.util.Map;

public interface Proxy {
	public Object verify(String id,String pwd,Map<String,Object> attrs);
	public String encrypt_pwd(String pwd,Map<String,Object> attrs);
}
