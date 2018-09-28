package pms.util.db;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import pms.util.StrUtil;
import pms.util.comm.lambda.exception.Handler;
import pms.util.comm.lambda.exception.SimpleExec;
import pms.util.comm.lambda.param.ParamWrapper;
import pms.util.db.parser.SQLTableParserImpl;
import pms.util.file.FileUtil;
import pms.util.reflect.Reflector;
import pms.util.schedule.Scheduler;
import pms.util.system.hook.HookUtil;

public class DBUtil {
	private static Scheduler scheduler;
	private static boolean active = false;
	private static int connCount;
	private static Connection conn;
	private static ReentrantLock lock;
	private static Driver driver;
	private static String os = null;
	// private static SQLTableParserImpl parser = new SQLTableParserImpl();
	static {
		lock = new ReentrantLock(true);
		driver = new Driver();
		os = System.getProperty("os.name").toLowerCase();
		HookUtil.addHook(()->{
			DBUtil.closeNow();
		});
	}

	public static void init() throws Exception {
		driver.configure("pms/util/db/db.properties");
	}

	public static Connection getConnection() {
		try {
			lock.lock();
			++connCount;
			//System.out.println(conn);
			if (conn != null) {
				return conn;
			}
			conn = getNewConnection();
			conn.setAutoCommit(true);
			if (conn != null) {
				active = true;
			}
			// System.out.println(conn);
		} catch (Exception e) {
			conn = null;
		} finally {
			lock.unlock();
		}
		return conn;
	}

	private static Connection getNewConnection() throws Exception {
		if (!active) {
			init();
		}
		return (Connection) driver.getConnection();
	}

	public static boolean createTable(String sql) {
		getConnection();
		try {
			return conn.createStatement().execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public static boolean insert(String sql) {
		try {
			Statement state = (Statement) getConnection().createStatement();
			return state.executeUpdate(sql)>0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("finally")
	public static ResultSet query(String sql) {
		getConnection();
		ResultSet rs = null;
		try {
			//System.out.println(sql);
			Statement state = (Statement) getConnection().createStatement();
			rs = state.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			return rs;
		}

	}

	public static PreparedStatement prepareSQL(String sql) {
		PreparedStatement ps;
		try {
			ps = (PreparedStatement) getConnection().prepareStatement(sql);
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
			ps = null;
		}
		return ps;
	}

	public static boolean update(String sql) {
		// System.out.println(sql);
		// boolean suc = true;
		try {
			Statement state = (Statement) getConnection().createStatement();
			return state.executeUpdate(sql) > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		// return suc;
	}

	public static Transaction transaction() {
		return new Transaction().begin();
	}

	public static boolean keyDel(String table, KV kv) {
		String sql = String.format("delete from %s where %s", table, kv);

		return DBUtil.update(sql);
	}

	public static boolean keyDel(String table, Keys kv) {
		String sql = String.format("delete from %s where %s", table, kv);

		return DBUtil.update(sql);
	}

	/**
	 * backup orcle with user models
	 * 
	 * @param name
	 *            username
	 * @param pwd
	 *            userpwd
	 * @param path
	 *            export path
	 * @param buffer
	 *            the size of buffer
	 */
	public static void backup_exp(String path, int buffer) {
		String cmd = os.contains("windows")
				? String.format("cmd /c start cmd.exe /c exp %s/%s@XE buffer=%s file=%s grants=y owner=%s",
						driver.getUsername(), driver.getPassword(), path, buffer, driver.getUsername())
				: os.contains("linux") ? String.format("exp %s/%s@XE buffer=%s file=%s grants=y owner=%s",
						driver.getUsername(), driver.getPassword(), path, buffer, driver.getUsername()) : "exit";

		SimpleExec.exec((data) -> {
			Process process = Runtime.getRuntime().exec(cmd);
			data.setValue(process);
			process.waitFor();
			return null;
		}, Handler.PRINTTRACE, (data) -> {
			((Process) data.getValue()).destroy();
		});
	}

	/**
	 * restore oracle database
	 * 
	 * @param name
	 *            username
	 * @param pwd
	 *            userpwd
	 * @param path
	 *            export path
	 * @param buffer
	 *            the size of buffer
	 */
	public static void restore_imp(String path, int buffer) {
		String cmd = os.contains("windows")
				? String.format("cmd /c start cmd.exe /c imp %s/%s  file=%s buffer=%s ignore=Y", driver.getUsername(),
						driver.getPassword(), path, buffer, driver.getUsername())
				: os.contains("linux")
						? String.format("imp %s/%s  file=%s buffer=%s ignore=Y", driver.getUsername(),
								driver.getPassword(), path, buffer, driver.getUsername())
						: "exit";
		SimpleExec.exec((data) -> {
			Process process = Runtime.getRuntime().exec(cmd);
			data.setValue(process);
			process.waitFor();
			return null;
		}, Handler.PRINTTRACE, (data) -> {
			((Process) data.getValue()).destroy();
		});

	}

	public static void backup_exp_oracle_tables(String path, int buffer, ArrayList<String> tables) {
		String tables_str = tables.stream().collect(Collectors.joining(","));
		String cmd = os.contains("windows")
				? String.format("cmd /c start cmd.exe /c exp %s/%s@XE buffer=%s file=%s tables=(%s)",
						driver.getUsername(), driver.getPassword(), path, tables_str)
				: os.contains("linux") ? String.format("exp %s/%s@XE buffer=%s file=%s tables=(%s)",
						driver.getUsername(), driver.getPassword(), path, tables_str) : "exit";
		SimpleExec.exec((data) -> {
			Process process = Runtime.getRuntime().exec(cmd);
			data.setValue(process);
			process.waitFor();
			return null;
		}, Handler.PRINTTRACE, (data) -> {
			((Process) data.getValue()).destroy();
		});
	}

	public static void initSheduler() {
		scheduler = new Scheduler().init(10, true);
	}

	public static void sheduleBackup(int timeout, String path) {
		scheduler.scheduleControllable(() -> {
			backup_exp(path, 1024);
		}, timeout, TimeUnit.SECONDS, "back_up");
	}

	public static void shutdownBackup() {
		scheduler.shutdown("back_up");
	}

	public static void close() {
		try {
			lock.lock();
			--connCount;
			if (conn != null && connCount == 0) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					conn = null;
					// active=false;
				}
			}
		} finally {
			lock.unlock();
		}
	}
	
	public static void closeNow() {
		SimpleExec.exec((data)->{
			conn.close();
			return null;
		}, Handler.PRINTTRACE);
	}

	public static boolean ifSatisfy(String table, Keys keys) {
		String sql = "select * from %s where %s";
		ResultSet rs = DBUtil.query(String.format(sql, table, keys));
		try {
			return rs.next();
		} catch (SQLException e) {
			return false;
		}
	}

	public static ResultSet page(String table, String col, boolean asc, int start, int size) {
		String sql = "select * from %s order by %s %s  limit %s,%s";
		sql = String.format(sql, table, col, asc ? "asc" : "desc", start, size);
		return DBUtil.query(sql);
	}

	public static boolean insertValues(String table, KV... values) {
		DBUtil.insertValues("table", new KV("",""),new KV("",""));
		String sql = String.format("insert into %s(%s) values(%s)", table,
				Arrays.stream(values).map(kv -> kv.key).collect(Collectors.joining(",")),
				Arrays.stream(values).map(kv -> "'"+kv.value.toString()+"'").collect(Collectors.joining(",")));
		return DBUtil.insert(sql);
	}

	public static boolean insertObject(Object o, Class<?> clazz, String type) {
		HashMap<String, Field> fields = Reflector.getFields(clazz);
		String[] fields_arr = fields.keySet().toArray(new String[0]);
		int size = fields_arr.length;
		StringBuilder sql = new StringBuilder("insert into ");
		sql.append(type);
		sql.append(String.format("(%s)", Arrays.stream(fields_arr).collect(Collectors.joining(","))));
		sql.append(" values(");
		SimpleExec.exec(data->{
			for (int i = 0; i < size; ++i) {
				Object value = Reflector.getter(clazz, fields_arr[i]).invoke(o);
				sql.append("'" + (value == null ? "" : value.toString()) + "'");
				if (i != size - 1)
					sql.append(",");
			}
			return null;
		}, Handler.PRINTTRACE);
	
		sql.append(")");
		return DBUtil.insert(sql.toString());
	}

	public static ArrayList<String> columnsOf(ResultSet rs) {
		ArrayList<String> cols = new ArrayList<>();
		try {
			ResultSetMetaData rsm = rs.getMetaData();
			int count = rsm.getColumnCount();
			for (int i = 1; i <= count; ++i) {
				cols.add(rsm.getColumnName(i).toLowerCase());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cols;
	}

	public static Object parse(ResultSet rs, Class<?> clazz) {
		return SimpleExec.exec((data) -> {
			if (rs == null || !rs.next()) {
				return null;
			}

			ArrayList<String> cols = DBUtil.columnsOf(rs);
			Object inst = clazz.getConstructor().newInstance();

			HashMap<String, Field> fields = Reflector.getFields(clazz);
			fields.values().stream().forEach(field -> {
				String name = field.getName();
				if (cols.contains(name.toLowerCase())) {
					SimpleExec.exec((wrapper) -> {
						Reflector.setter(clazz, name).invoke(inst, rs.getString(name));
						// System.out.println(name+"="+ rs.getString(name));
						return null;
					}, Handler.PRINTTRACE);
				}
			});
			return inst;
		}, Handler.PRINTTRACE);
	}

	public static ArrayList<Object> toArrayList(ResultSet rs, Class<?> clazz) {
		ArrayList<Object> list = new ArrayList<>();
		Object obj = new Object();
		while (rs != null && obj != null) {
			obj = DBUtil.parse(rs, clazz);
			if (obj != null) {
				list.add(obj);
			}
		}
		return list;
	}
	
	public static ArrayList<String> toArrayListOf(String col,ResultSet rs){
		ArrayList<String> list = new ArrayList<>();
		SimpleExec.exec(data->{
			while (rs.next()) {
				list.add(rs.getString(col));
			}
			return null;
		}, e->{
			e.printStackTrace();
		});
		
		return list;
	}
	
	public static String max(String table,String col,String default_value) {
		ResultSet rs=DBUtil.query(SQL.create().selectMax(col).from(table).complete());
		String max=(String) SimpleExec.exec(data->{
			if (rs.next()){
			return rs.getString(1);
		}
			return default_value;
		}, Handler.PRINTTRACE);
		
		return max==null?default_value:max;
	}

	public static ResultSet keyQuery(String table, KV kv) {
		String sql = String.format("select * from %s where %s", table, kv);
		return DBUtil.query(sql);
	}

	public static ResultSet keyOrderedQuery(String table, KV kv, String col, boolean asc) {
		String sql = String.format("select * from %s where %s order by %s %s", table, kv, col, asc ? "asc" : "desc");
		return DBUtil.query(sql);
	}

	public static ResultSet keysQuery(String table, Keys keys) {
		String sql = String.format("select * from %s where %s", table, keys);
		return DBUtil.query(sql);
	}

	public static ResultSet keysOrderedQuery(String table, Keys kv, String col, boolean asc) {
		String sql = String.format("select * from %s where %s order by %s %s", table, kv, col, asc ? "asc" : "desc");
		return DBUtil.query(sql);
	}

	public static boolean keysQueryNotEmpty(String table, Keys keys) {
		String sql = String.format("select * from %s where %s", table, keys);
		try {
			return DBUtil.query(sql).next();
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean update(String table, KV key, KV... args) {
		String set = Arrays.stream(args).map(KV::toString).collect(Collectors.joining(","));
		String sql = String.format("update %s set %s where %s", table, set, key);
		// System.out.println(sql);
		return DBUtil.update(sql);
	}

	public static boolean updateKeys(String table, Keys keys, KV... kvs) {
		//where 1=2
		//update("",new Keys().start(new KV("age","").op("》")).and(new KV()),)
		String set = Arrays.stream(kvs).map(KV::toString).collect(Collectors.joining(","));
		String sql = String.format("update %s set %s where %s", table, set, keys);

		return DBUtil.update(sql);
	}

	public static Map<String, Object> rsToMap(ResultSet rs, String[] cols) {
		Map<String, Object> map = new HashMap<>();

		if (rs == null) {
			return map;
		}

		try {
			while (rs.next()) {
				Arrays.stream(cols).forEach(col -> {
					try {
						map.put(col, rs.getObject(col));
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return map;
	}

	public static void selectRowsByRecursion(String table, RecursionResultSet rs, Class<?> clazz) {
		KV key_kv = rs.getKey_kv();
		KV link_kv = rs.getLink_kv();
		String sql = String.format("select * from %s where %s='%s'", table, link_kv.key, key_kv.value);
		ArrayList<Object> ls_obj = DBUtil.toArrayList(DBUtil.query(sql), clazz);
		ArrayList<RecursionResultSet> ls_rs = new ArrayList<>();
		ls_obj.stream().forEach(obj -> {
			try {
				RecursionResultSet rrs = new RecursionResultSet(clazz);
				rrs.setObj(obj);
				String key_v = clazz
						.getDeclaredMethod(String.format("get%s", StrUtil.firstLetterToUpperCase(key_kv.key)))
						.invoke(obj).toString();
				String link_v = clazz
						.getDeclaredMethod(String.format("get%s", StrUtil.firstLetterToUpperCase(link_kv.key)))
						.invoke(obj).toString();
				rrs.setKey_kv(new KV(key_kv.key, key_v));
				rrs.setLink_kv(new KV(link_kv.key, link_v));
				ls_rs.add(rrs);
				selectRowsByRecursion(table, rrs, clazz);
			} catch (Exception e) {
				// TODO Auto-=generated catch block
				e.printStackTrace();
			}
		});
		rs.setList(ls_rs);
	}

	public static RecursionResultSet selectRowsByRecursion(String table, KV key_kv, KV link_kv, Class<?> clazz)
			throws Exception {
		RecursionResultSet rrs = new RecursionResultSet(clazz);
		rrs.setKey_kv(key_kv);
		rrs.setLink_kv(link_kv);
		rrs.setObj(DBUtil.parse(DBUtil.keyQuery("msg", key_kv), clazz));
		selectRowsByRecursion(table, rrs, clazz);
		return rrs;
	}

	public static class KV {
		public String key;
		public Object value;
		public String op = "=";
		private boolean unwrap = false;

		public KV() {
		}

		public KV(String k, Object v) {
			this.key = k;
			this.value = v;
		}

		public KV(String k, Object v, String op) {
			this.key = k;
			this.value = v;
			this.op = op;
		}

		public KV unwrap() {
			unwrap = true;
			return this;
		}

		public KV op(String op) {
			this.op=op;
			return this;
		}
		public String toString() {
			return unwrap ? key + op + value : key + op + "'" + value + "'";
		}

		public boolean equals(KV kv) {
			return this.key.equals(kv.key);
		}
	}

	public static class Keys {
		private ArrayList<KV> kvs = new ArrayList<>();
		private StringBuilder where = new StringBuilder();

		public Keys start(KV kv) {
			if (kvs.size() == 0) {
				kvs.add(kv);
				where.append("%s");
			}
			return this;
		}

		public Keys and(KV kv) {
			kvs.add(kv);
			if (!kvs.isEmpty()) {
				where.append(" and %s");
			}
			return this;
		}

		public Keys or(KV kv) {
			kvs.add(kv);
			if (!kvs.isEmpty()) {
				where.append(" or %s");
			}
			return this;
		}

		public String complete() {
			return String.format(where.toString(), kvs.toArray());
		}

		public String toString() {
			return this.complete();
		}
	}

	public static class RecursionResultSet {
		private Object obj;
		private transient KV key_kv;
		private transient KV link_kv;
		private ArrayList<RecursionResultSet> list;

		public KV getKey_kv() {
			return key_kv;
		}

		public void setKey_kv(KV key_kv) {
			this.key_kv = key_kv;
		}

		public KV getLink_kv() {
			return link_kv;
		}

		public void setLink_kv(KV link_kv) {
			this.link_kv = link_kv;
		}

		public RecursionResultSet(Class<?> clazz) throws Exception {
			obj = clazz.getDeclaredConstructor().newInstance();
		}

		public Object getObj() {
			return obj;
		}

		public void setObj(Object obj) {
			this.obj = obj;
		}

		public ArrayList<RecursionResultSet> getList() {
			return list;
		}

		public void setList(ArrayList<RecursionResultSet> list) {
			this.list = list;
		}
	}

	/**
	 * 执行事务相关类
	 * 
	 * @author delin
	 *
	 */
	public static class Transaction {
		private ArrayList<String> sqls = new ArrayList<>();
		private Connection conn;

		public Transaction begin() {
			try {
				conn = getNewConnection();
				conn.setAutoCommit(false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			return this;
		}

		public Transaction add(SQL sql) {
			sqls.add(sql.complete());

			return this;
		}

		public Transaction add(String sql) {
			sqls.add(sql);

			return this;
		}

		public boolean commit() {
			ParamWrapper wrapper=ParamWrapper.instance();
			Statement state=null;
			try {
				state= (Statement) conn.createStatement();
				wrapper.set(state);
				
				for (String sql : sqls) {
					//System.out.println(sql);
					state.addBatch(sql.toString());
				}

				state.executeBatch();
				conn.commit();
				return true;
			} catch (SQLException e) {
				SimpleExec.exec(data->{
					conn.rollback();
					return null;
				},Handler.CARELESS);
				e.printStackTrace();
				return false;
			}finally {
				SimpleExec.exec(data->{
					if (wrapper.get()!=null) {
						((Statement)wrapper.get()).close();
					}
					if (conn!=null) {
						conn.close();
					}
					conn=null;
					return null;
				},Handler.PRINTTRACE);
			}
		}
	}

	public static class SQL {
		private StringBuilder sql = new StringBuilder();

		private SQL() {
		}

		public static SQL create() {
			return new SQL();
		}

		public SQL select(KV... kvs) {
			sql.append("select ");
			if (kvs == null) {
				sql.append("* ");
			} else {
				sql.append(Arrays.stream(kvs).map(kv -> {
					kv.op = ".";
					return kv.toString();
				}).collect(Collectors.joining(",")));
				sql.append(" ");
			}
			return this;
		}
		
		public SQL selectMax(String col) {
			sql.append("select max(");
			sql.append(col);
			sql.append(")");
			sql.append(" ");
			return this;
		}

		public SQL update(String table) {
			sql.append("update ");
			sql.append(table);
			sql.append(" ");
			return this;
		}

		public SQL insert(String table) {
			sql.append("insert into ");
			sql.append(table);
			return this;
		}

		public SQL delete(String table) {
			sql.append("delete from ");
			sql.append(table);
			sql.append(" ");
			return this;
		}

		public SQL values(KV... kvs) {
			sql.append(Arrays.stream(kvs).map(kv -> kv.key).collect(Collectors.joining(",", "(", ")")));
			sql.append(" ");
			sql.append("values");
			sql.append(Arrays.stream(kvs).map(kv -> "'" + kv.value + "'").collect(Collectors.joining(",", "(", ")")));

			return this;
		}

		public SQL set(KV... kvs) {
			sql.append("set ");
			sql.append(Arrays.stream(kvs).map(KV::toString).collect(Collectors.joining(",")));
			sql.append(" ");

			return this;
		}

		public SQL from(String... tables) {
			String from = Arrays.stream(tables).collect(Collectors.joining(","));
			sql.append("from ");
			sql.append(from);
			sql.append(" ");
			return this;
		}

		public SQL where(Keys keys) {
			sql.append("where ");
			sql.append(keys.complete());
			sql.append(" ");
			return this;
		}

		public SQL like(String like) {
			sql.append("like ");
			sql.append(like);
			sql.append(" ");
			return this;
		}

		public SQL orderBy(String... props) {
			sql.append("order by ");
			sql.append(Arrays.stream(props).collect(Collectors.joining(",")));
			sql.append(" ");
			return this;
		}

		public String ASC() {
			sql.append("asc");
			return sql.toString();
		}

		public String desc() {
			sql.append("desc");
			return sql.toString();
		}

		public String complete() {
			return sql.toString();
		}

		public String toString() {
			return this.complete();
		}

		public String insertObject(String table, Object o) {
			StringBuilder sql = new StringBuilder();
			HashMap<String, Field> fields = Reflector.getFields(o.getClass());
			sql.append("insert into ");
			sql.append(table);
			StringBuilder values = new StringBuilder("values(");
			StringBuilder cols = new StringBuilder("(");
			fields.entrySet().stream().forEach(entry -> {
				SimpleExec.exec((data) -> {
					values.append("'");
					values.append(entry.getValue().get(o));
					values.append("'");
					values.append(",");
					cols.append(entry.getKey());
					cols.append(",");
					return null;
				}, Handler.PRINTTRACE);
			});
			values.replace(values.length() - 1, values.length(), ")");
			cols.replace(cols.length() - 1, cols.length(), ")");
			sql.append(cols);
			sql.append(" ");
			sql.append(values);

			return sql.toString();
		}

		public String updateObject(String table, Object o, Keys kv) {
			HashMap<String, Field> fields = Reflector.getFields(o.getClass());
			StringBuilder sql = new StringBuilder();
			sql.append("update ");
			sql.append(table);
			sql.append(" set ");
			StringBuilder sets = new StringBuilder();
			fields.entrySet().stream().forEach(entry -> {
				SimpleExec.exec((data) -> {
					sets.append(new KV(entry.getKey(), entry.getValue().get(o)));
					sets.append(",");
					return null;
				}, Handler.PRINTTRACE);
			});
			sets.delete(sets.length() - 1, sets.length());
			sql.append(sets);
			sql.append(" ");
			sql.append("where ");
			sql.append(kv);

			return sql.toString();
		}
	}

	public static ResultSet queryAll(String table, String[] cols) {
		KV[] kvs = null;
		if (cols != null) {
			kvs = Arrays.stream(cols).map(col -> new KV(table, col)).toArray(KV[]::new);
		}
		return DBUtil.query(SQL.create().select(kvs).from(table).complete());
	}
	
	public static void main(String... args) throws Exception {
		DBUtil.init();
		testTable();
	}
	
	public static void testTable() throws IOException {
		String text = FileUtil.readText("pms/pms.sql", true);
		SQLTableParserImpl parser=new SQLTableParserImpl();

		String[] sqls = text.split(";");
		Arrays.stream(sqls).forEach(sql -> {
			//sSystem.out.println(sql);
			parser.parse(sql);
			try {
				FileUtil.writeText("pms/bean",StrUtil.firstLetterToUpperCase(parser.getTable())+".java", parser.toBean(sql));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
}
