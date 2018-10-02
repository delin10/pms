package pms.util.redis.cahce;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import pms.util.auth.Getter;
import pms.util.comm.lambda.exception.Handler;
import pms.util.comm.lambda.exception.SimpleExec;
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
	private RedisDriver driver = new RedisDriver();
	private static Scheduler scheduler = new Scheduler().init(10, true);
	private static HashMap<String, CacheSwap> swaps = new HashMap<>();
	private static ReentrantLock lock = new ReentrantLock(true);
	private static final String TEMP_KEY = "cache_rw";
	{
		DriverConfig conf = new DriverConfig();
		try {
			conf.load("pms/conf/redis.props");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// driver = new RedisDriver();
		driver.getInstance(conf);
		HookUtil.addHook(() -> {
			System.out.println("exit...");
			scheduler.shutdownNow();
			swaps.values().forEach(swap -> swap.destroy());
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

	public class CacheSwap {
		private int MAX_SIZE = 20;
		private int REFRESH_TIME = 10;
		private String table;
		private Class<?> row;
		private String id;
		private Set<String> update_id = new HashSet<>();
		private Controllable thread;
		private String redis_key;
		private boolean MAX_ID;
		private String MAX_ID_KEY = "MAX_ID";
		private String cache_sql = null;

		/**
		 * @param table
		 *            关联的表名
		 * @param id
		 *            关联的表的唯一主键
		 * @param row
		 *            表行关联的bean对象
		 */
		public CacheSwap(String table, String id, Class<?> row) {
			this.table = table;
			this.row = row;
			this.id = id;
		}

		public void cacheAll() {
			// System.out.println(cache_sql);
			ResultSet rs = cache_sql == null ? DBUtil.queryAll(table, null) : DBUtil.query(cache_sql);
			Object o = DBUtil.parse(rs, row);
			try {
				while (o != null) {
					String key = "" + Reflector.get(o, id);
					// System.out.println("key:"+key);
					driver.set(table, key, o);
					o = DBUtil.parse(rs, row);
				}
				if (MAX_ID) {
					SimpleExec.exec(data -> {
						driver.set_default(table, MAX_ID_KEY, DBUtil.max(table, id, "0"));
						return null;
					}, Handler.PRINTTRACE);

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public ArrayList<Object> all() {
			return driver.keys(table + "*").stream().filter(key -> !key.contains(MAX_ID_KEY)).map(driver::get)
					.collect(Collectors.toCollection(ArrayList<Object>::new));
		}

		public String getCache_sql() {
			return cache_sql;
		}

		public void setCache_sql(String cache_sql) {
			this.cache_sql = cache_sql;
		}

		public String getMaxId() {
			return (String) driver.get(table, MAX_ID_KEY);
		}

		public String incMaxId() {
			return driver.inc(table, MAX_ID_KEY);
		}

		public boolean updateCache(Object o) {
			Object key = null;
			try {
				key = Reflector.get(o, id);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			if (key == null) {
				return false;
			}
			String id = key.toString();
			String key_str = driver.combine(table, id);
			try {
				lock.lock();
				if (record(key_str, driver.contains(key_str) ? "update" : "insert")) {
					// 如果存在则无法添加
					update_id.add(id);
					return driver.set(key_str, o);
				}
				return false;
			} finally {
				lock.unlock();
			}
		}

		public boolean deleteCache(String id) {
			try {
				lock.lock();
				String key_str = driver.combine(table, id);
				if ("insert".equals(driver.get(TEMP_KEY, key_str))) {
					driver.remove(TEMP_KEY, key_str);
					driver.remove(key_str);
					return true;
				}
				update_id.add(id);
				return record(id, "delete");
			} finally {
				lock.unlock();
			}
		}

		public boolean record(String key, String op) {
			if ("delete".equals(op)) {
				return driver.set(TEMP_KEY, key, op);
			}

			if ("insert".equals(driver.get(TEMP_KEY, key))) {
				return true;
			}
			return driver.set(TEMP_KEY, key, op);
		}

		public Object get(String id) {
			return driver.get(table, id);
		}

		public void sync() {
			synchronized (driver) {
				Transaction trans = DBUtil.transaction();
				trans.begin();
				update_id.forEach(id -> {
					String key = driver.combine(table, id);
					String op = "" + driver.get(TEMP_KEY, key);
					Object data = driver.get(key);
					if ("insert".equals(op)) {
						String sql = DBUtil.SQL.create().insertObject(table, data);
						trans.add(sql);
					} else if ("update".equals(op)) {
						String sql = DBUtil.SQL.create().updateObject(table, data,
								new Keys().start(new KV(this.id, id)));
						trans.add(sql);
					} else if ("delete".equals(op)) {
						String sql = DBUtil.SQL.create().delete(table).where(new Keys().start(new KV(this.id, id)))
								.complete();
						trans.add(sql);
					}

					driver.remove(TEMP_KEY, key);
				});
				trans.commit();
				update_id.clear();
			}
		}

		public void destroy() {
			sync();
			driver.keys(table + "*").stream().forEach(driver::remove);
			driver.remove(table, MAX_ID_KEY);
			// driver.keys(TEMP_KEY+"*").stream().forEach(driver::remove);
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

		public boolean isMAX_ID() {
			return MAX_ID;
		}

		public void setMAX_ID(boolean mAX_ID) {
			MAX_ID = mAX_ID;
		}
	}

	public static void main(String... args) throws InterruptedException, IOException {
		System.out.println(Getter.cache.get("roles").incMaxId());
	}
}
