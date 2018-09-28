package pms.util.redis.driver;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Semaphore;

import pms.util.serialize.SerializeUtil;
import pms.util.system.hook.HookUtil;
import redis.clients.jedis.Jedis;

public class RedisDriver {
	private static int MAX_CONN=10;
	private static Semaphore out;
	private static Jedis jedis;
	static {
		out=new Semaphore(MAX_CONN);
	}
	public RedisDriver() {
		try {
			out.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean ready() {
		return jedis!=null && jedis.isConnected();
	}
	public  Jedis getInstance() {
		return jedis;
	}
	
	public static Jedis getInstance(String ip) {
		if (jedis==null)
			jedis=new Jedis(ip);
		return jedis;
	}

	public static Jedis getInstance(String ip, int port) {
		if (jedis==null)
			jedis=new Jedis(ip,port);
		return jedis;
	}
	
	public static Jedis getInstance(DriverConfig config) {
		if (jedis==null)
			jedis=new Jedis(config.getIp(),config.getPort());
		return jedis;
	}
	
	public  boolean set(Object k,Object v) {
		String res=jedis.set(SerializeUtil.serialize(k),SerializeUtil.serialize(v));
		return "ok".equalsIgnoreCase(res);
	}
	
	public  boolean set(String k,String v) {
		String res=jedis.set(k.getBytes(),v.getBytes());
		return "ok".equalsIgnoreCase(res);
	}
	
	public  boolean set(String k,Object v) {
		//System.out.println(jedis);
		String res=jedis.set(k.getBytes(),SerializeUtil.serialize(v));
		return "ok".equalsIgnoreCase(res);
	}
	
	public boolean set(String table,String id,Object v) {
		return set(combine(table,id),v);
	}
	
	public boolean set_default(String table,String id,String v) {
		return "ok".equals(jedis.set(combine(table,id), v));
	}
	
	public Object get(String table,String id) {
		return get(combine(table,id));
	}
	
	public String combine(String table,String key) {
		return String.format("%s:%s", table,key);
	}
	
	public  Object get(Object k) {
		return SerializeUtil.resolve(jedis.get(SerializeUtil.serialize(k)));
	}
	
	public  Object get(String k) {
		return SerializeUtil.resolve(jedis.get(k.getBytes()));
	}
	
	public void lpush(String table,String key,Object...objs) {
		String keys=combine(table,key);
		byte[][] bytes=Arrays.stream(objs).map(SerializeUtil::serialize).toArray(byte[][]::new);
		jedis.lpush(keys.getBytes(),bytes);
	}
	
	public void rpush(String table,String key,Object...objs) {
		String keys=combine(table,key);
		byte[][] bytes=Arrays.stream(objs).map(SerializeUtil::serialize).toArray(byte[][]::new);
		jedis.rpush(keys.getBytes(),bytes);
	}
	
	public ArrayList<Object> lpop(String table,String key){
		ArrayList<Object> list=new ArrayList<>();
		byte[] keys=combine(table,key).getBytes();
		byte[] res;
		while((res=jedis.lpop(keys)).length!=0) {
			list.add(SerializeUtil.resolve(res));
		}
		return list;
	}
	
	public ArrayList<Object> rpop(String table,String key){
		ArrayList<Object> list=new ArrayList<>();
		byte[] keys=combine(table,key).getBytes();
		byte[] res;
		while((res=jedis.rpop(keys)).length!=0) {
			list.add(SerializeUtil.resolve(res));
		}
		return list;
	}
	
	public void zadd(String table,String key,double standard, Object obj) {
		byte[] keys=combine(table,key).getBytes();
		jedis.zadd(keys, standard, SerializeUtil.serialize(obj));
	}
	
	public void remove(String key) {
		//System.out.println(key);
		jedis.del(key);
	}
	
	public void remove(String table,String key) {
		jedis.del(combine(table,key));
	}
	
	public boolean contains(String key) {
		//System.out.println(jedis.get(key));
		return jedis.get(key)!=null;
	}
	
	public boolean expire(String key,int timeout) {
		return jedis.expire(key, timeout)==1;
	}
	
	public boolean expire(String table,String key,int timeout) {
		return jedis.expire(this.combine(table, key), timeout)==1;
	}
	
	public Set<String> keys(String pattern){
		return jedis.keys(pattern);
	}
	
	public String inc(String key) {
		return ""+jedis.incr(key);
	}
	
	public String inc(String table,String key) {
		return ""+jedis.incr(combine(table,key));
	}
	
	public void close() {
		if (out.availablePermits()==MAX_CONN)
			jedis.close();
	}
	
	public void close(RedisHook hook) {
		hook.hook();
		close();
	}
	
	
	public static void main(String...args) throws IOException {
		DriverConfig conf=new DriverConfig();
		conf.load("pms/conf/redis.props");
		RedisDriver.getInstance(conf);
		System.out.println(jedis.keys("roles*"));
		HookUtil.addHook(()->{
			System.out.println("hook");
		});
	}
	
	public static interface RedisHook{
		public void hook();
	}
	public static class DriverConfig{
		private String ip=null;
		private int port=0;
		private String password=null;
		
		public void load(String path) throws IOException {
			Properties props=new Properties();
			try(InputStream input=RedisDriver.class.getClassLoader().getResourceAsStream(path)){
				props.load(input);
			}
			
			setIp(props.getProperty("jedis-ip","localhost"));
			setPort(Integer.parseInt(props.getProperty("jedis-port","6083")));
			setPassword(props.getProperty("jedis-pwd"));
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
}
