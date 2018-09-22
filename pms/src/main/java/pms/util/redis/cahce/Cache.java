package pms.util.redis.cahce;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import pms.util.auth.bean.Role;
import pms.util.db.DBUtil;
import pms.util.db.DBUtil.KV;
import pms.util.db.DBUtil.Keys;
import pms.util.db.DBUtil.Transaction;
import pms.util.redis.driver.RedisDriver;
import pms.util.redis.driver.RedisDriver.DriverConfig;
import pms.util.reflect.Reflector;
import pms.util.schedule.Controllable;
import pms.util.schedule.Scheduler;
import pms.util.system.hook.HookUtil;

public class Cache {
	private static RedisDriver driver = new RedisDriver();
	private static Scheduler scheduler = new Scheduler().init(10,true);
	private static HashMap<String, CacheSwap> swaps = new HashMap<>();
	private static final String TEMP_KEY = "cache_rw";
	static {
		DriverConfig conf = new DriverConfig();
		try {
			conf.load("pms/conf/redis.props");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//driver = new RedisDriver();
		RedisDriver.getInstance(conf);
		HookUtil.addHook(()->{
			System.out.println("exit...");
			scheduler.shutdownNow();
			swaps.values().forEach(swap->swap.destroy());
		});
	}

	public CacheSwap stop(String id) {
		CacheSwap swap = swaps.get(id);
		if (swap != null) {
			swap.stop();
		}
		return swap;
	}

	public Cache register(CacheSwap swap) {
		if (swaps.containsKey(swap.table)) {
			return this;
		}
		swaps.put(swap.table, swap);
		swap.cacheAll();
		swap.start();
		return this;
	}

	public CacheSwap get(String table) {
		return swaps.get(table);
	}

	public Cache remove(String table) {
		swaps.remove(table);
		return this;
	}

	public static class CacheSwap {
		private int MAX_SIZE = 20;
		private int REFRESH_TIME = 10;
		private String table;
		private Class<?> row;
		private String id;
		private Set<String> update_id = new HashSet<>();
		private Controllable thread;

		public CacheSwap(String table, String id, Class<?> row) {
			this.table = table;
			this.row = row;
			this.id = id;
		}

		public void cacheNow() {

		}

		public void cacheAll() {
			ResultSet rs = DBUtil.queryAll(table, null);
			Object o = DBUtil.parse(rs, row);
			try {
				while (o != null) {
					String key = ""+Reflector.get(o, id);
					System.out.println(key);
					driver.set(table, key, o);
					o = DBUtil.parse(rs, row);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void updateCache(Object o) {
			Object key = null;
			try {
				key = Reflector.get(o, id);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			if (key == null) {
				return;
			}
			String id=key.toString();
			String key_str = driver.combine(table, id);
			record(key_str, driver.contains(key_str) ? "update" : "insert");
			update_id.add(id);
			driver.set(key_str, o);
		}

		public void deleteCache(Object o) {
			String key = null;
			try {
				key = Reflector.get(o, id).toString();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			update_id.add(key);
			record(key, "delete");
		}

		public void record(String key, String op) {
			if ("delete".equals(op)) {
				driver.set(TEMP_KEY, key, op);
				return;
			}

			if ("insert".equals(driver.get(TEMP_KEY, key))) {
				return;
			}
			driver.set(TEMP_KEY, key, op);
		}

		public Object get(String id) {
			return driver.get(id);
		}

		public void sync() {
			Transaction trans = DBUtil.tansation();
			trans.begin();
			update_id.forEach(id -> {
				String key = driver.combine(table, id);
				String op = "" + driver.get(TEMP_KEY, key);
				Object data = driver.get(key);
				if ("insert".equals(op)) {
					String sql = DBUtil.SQL.create().insertObject(table, data);
					trans.add(sql);
				} else if ("update".equals(op)) {
					String sql = DBUtil.SQL.create().updateObject(table, data, new Keys().start(new KV(this.id, id)));
					trans.add(sql);
				} else if ("delete".equals(op)) {
					String sql = DBUtil.SQL.create().delete(table).where(new Keys().start(new KV(this.id, id)))
							.complete();
					trans.add(sql);
				}
				
				driver.remove(TEMP_KEY,key);
			});
			trans.commit();
			update_id.clear();

		}

		public void destroy() {
			driver.keys(table+"*").stream().forEach(driver::remove);
			driver.keys(TEMP_KEY+"*").stream().forEach(driver::remove);
		}

		public void start() {
			thread = scheduler.scheduleControllable(() -> {
				this.sync();
			}, REFRESH_TIME, TimeUnit.SECONDS);
		}

		public void stop() {
			thread.stop();
			thread = null;
		}

		public int getMAX_SIZE() {
			return MAX_SIZE;
		}

		public void setMAX_SIZE(int mAX_SIZE) {
			MAX_SIZE = mAX_SIZE;
		}

		public int getREFRESH_TIME() {
			return REFRESH_TIME;
		}

		public void setREFRESH_TIME(int rEFRESH_TIME) {
			REFRESH_TIME = rEFRESH_TIME;
		}

		public String getTable() {
			return table;
		}

		public void setTable(String table) {
			this.table = table;
		}

		public String getRedis_key() {
			return redis_key;
		}

		public void setRedis_key(String redis_key) {
			this.redis_key = redis_key;
		}

		private String redis_key;

	}

	public static void main(String... args) throws InterruptedException {
		Cache cache = new Cache();
		CacheSwap swap = new CacheSwap("roles", "id", Role.class);
		cache.register(swap);
		int id = 1;
		while (true) {
			Thread.sleep(3000);
			Role role = new Role();
			role.setId("" + id++);
			role.setAvailable("1");
			role.setDescription("≤‚ ‘");
			role.setName("≤‚ ‘");
			System.out.println(role.getId());
			cache.get("roles").updateCache(role);
		}
		//scheduler.shutdownNow();
	}
}
