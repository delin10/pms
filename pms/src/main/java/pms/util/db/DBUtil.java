package pms.util.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import pms.util.db.dict.sub.OracleDictionary;
import pms.util.file.FileUtil;
import pms.util.regex.PatternUtil;
import pms.util.string.StrUtil;

public class DBUtil {
	private static OracleDictionary dict = new OracleDictionary();
	private static int connCount;
	private static Connection conn;
	private static ReentrantLock lock;
	static {
		lock = new ReentrantLock(true);
	}

	@SuppressWarnings("finally")
	public static Connection getConnection() {
		try {
			lock.lock();
			++connCount;
			if (conn != null) {
				return conn;
			}
			Properties pros = new Properties();
			try (InputStream in = DBUtil.class.getResourceAsStream("db.properties")) {
				pros.load(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String driver = pros.getProperty("jdbc.driver");
			String url = pros.getProperty("jdbc.url");
			String username = pros.getProperty("jdbc.username");
			String password = pros.getProperty("jdbc.password");
			try {
				Class.forName(driver);
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			/**
			 * web 出问题
			 */
			/*
			 * if (driver != null) System.setProperty("jdbc.driver", driver);
			 */
			try {
				conn = (Connection) DriverManager.getConnection(url, username, password);
			} catch (SQLException e) {
				e.printStackTrace();
				conn = null;
			} finally {
				return conn;
			}
		} finally {
			lock.unlock();
		}
	}

	public static String[] createTablesFrom(String sql_file) throws IOException, SQLException {
		ArrayList<String> fail_tables = new ArrayList<>();
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(sql_file))) {
			StringBuilder text = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				text.append(line.trim());
			}

			getConnection();
			Statement state = conn.createStatement();
			String[] sqls = text.toString().split(";");
			for (String sql : sqls) {
				String table = DBUtil.getTableName(sql);
				System.out.println(table);
				if (DBUtil.isExist_oracle(table)) {
					fail_tables.add(table);
					continue;
				}
				state.execute(sql);
			}

		}
		return fail_tables.toArray(new String[0]);
	}

	private static boolean isExist_oracle(String name) throws SQLException {
		String sql = String.format("select * from tab where tname='%s'", name.toUpperCase());
		getConnection();
		return conn.createStatement().executeQuery(sql).next();
	}

	public static boolean insert(String sql) {
		getConnection();
		try {
			Statement state = (Statement) getConnection().createStatement();
			return state.execute(sql);
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
		boolean suc = true;
		try {
			Statement state = (Statement) getConnection().createStatement();
			suc = state.executeUpdate(sql) != 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			suc = false;
		}
		return suc;
	}

	public static boolean keyDel(String table, String key, String val) {
		String sql = String.format("delete from %s where %s = %s", table, key, val);

		return DBUtil.update(sql);
	}

	public static boolean keyDel(String table, KV... keys) {
		String sql = String.format("delete from %s where %s", table,
				Arrays.stream(keys).map(KV::toString).collect(Collectors.joining(" and ")));

		return DBUtil.update(sql);
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
				}
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 分页查询
	 * 
	 * @param table
	 * @param col
	 * @param start
	 * @param size
	 * @return
	 */
	public static ResultSet page(String table, String col, int start, int size) {
		return DBUtil.query(dict.page(table, col, start, size, true));
	}

	/**
	 * 插入列名，值
	 * 
	 * @param table
	 * @param values
	 * @return
	 */
	public static boolean insertValues(String table, KV... values) {
		String sql = String.format("insert into %s(%s) values(%s)", table,
				Arrays.stream(values).map(kv -> kv.key).collect(Collectors.joining(",")),
				Arrays.stream(values).map(kv -> kv.value.toString()).collect(Collectors.joining(",")));
		return DBUtil.insert(sql);
	}

	/**
	 * 插入对象 注意变量名和列名相同
	 * 
	 * @param o
	 * @param clazz
	 * @param table
	 * @return
	 */
	public static boolean insertObject(Object o, Class<?> clazz, String table) {
		Field[] fields = clazz.getDeclaredFields();
		int size = fields.length;
		Field.setAccessible(fields, true);
		StringBuilder sql = new StringBuilder("insert into ");
		sql.append(table);
		sql.append(String.format("(%s)", Arrays.stream(fields).map(Field::getName).collect(Collectors.joining(","))));
		sql.append(" values(");
		try {

			for (int i = 0; i < size; ++i) {
				Method method = clazz
						.getDeclaredMethod(String.format("get%s", StrUtil.firstLetterToUpperCase(fields[i].getName())));
				method.setAccessible(true);
				Object value = method.invoke(o);
				if (value instanceof Integer) {
					sql.append(value.toString());
				} else {
					sql.append(String.format("'%s'", value == null ? "" : value.toString()));
				}
				if (i != size - 1)
					sql.append(",");
			}
			sql.append(")");
			return DBUtil.insert(sql.toString());
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {
			DBUtil.close();
		}
	}

	/**
	 * 查看表的列名
	 * 
	 * @param rs
	 * @return
	 */
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

	/**
	 * 把结果集包装成一个对象 外部不需要调用rs.next()，否则会跳过数据
	 * 
	 * @param rs
	 * @param clazz
	 * @return
	 */
	public static Object parse(ResultSet rs, Class<?> clazz) {
		Object inst = null;

		try {
			if (rs == null || !rs.next()) {
				return inst;
			}

			ArrayList<String> cols = DBUtil.columnsOf(rs);
			inst = clazz.getConstructor().newInstance();

			Field[] fields = clazz.getDeclaredFields();
			int size = fields.length;
			Field.setAccessible(fields, true);
			for (int i = 0; i < size; ++i) {
				String fn = fields[i].getName();
				if (cols.contains(fn.toLowerCase())) {
					String value = rs.getObject(fn).toString();
					fields[i].set(inst, value.toString());
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException | SQLException | InstantiationException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return inst;
	}

	/**
	 * 把结果集转换成数组列表
	 * 
	 * @param rs
	 * @param clazz
	 * @return
	 */
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

	/**
	 * 查询单一主键的结果
	 * 
	 * @param table
	 * @param kv
	 * @return
	 */
	public static ResultSet keyQuery(String table, KV kv) {
		String sql = String.format("select * from %s where %s", table, kv);
		return DBUtil.query(sql);
	}

	/**
	 * 更新某一行多个值
	 * 
	 * @param table
	 * @param keys
	 *            查询条件名值对
	 * @param args
	 *            需要设置的名值对
	 * @return
	 */
	public static boolean updateKeys(String table, Keys keys, KV... kv) {
		String set = Arrays.stream(kv).map(KV::toString).collect(Collectors.joining(","));
		String sql = String.format("update %s set %s where %s", table, set, keys);

		return DBUtil.update(sql);
	}

	public static Map<String, Object> rsToMap(ResultSet rs) {
		Map<String, Object> map = new HashMap<>();

		if (rs == null) {
			return map;
		}

		ArrayList<String> cols = DBUtil.columnsOf(rs);
		try {
			while (rs.next()) {
				cols.stream().forEach(col -> {
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

	/**
	 * 递归查询子分支
	 * 
	 * @param table
	 * @param rs
	 * @param clazz
	 */
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
		rrs.setObj(DBUtil.parse(DBUtil.keyQuery(table, key_kv), clazz));
		selectRowsByRecursion(table, rrs, clazz);
		return rrs;
	}

	public static String getTableName(String create_sql) {
		return PatternUtil.getSubString(create_sql, "create\\s+table\\s+(.*?)\\s*\\(");
	}

	public static class KV {
		public String key;
		public Object value;
		public String op = "=";

		public KV() {
		}

		public KV(String k, Object v) {
			this.key = k;
			this.value = v;
		}

		public String toString() {
			return key + op + value;
		}

		public boolean equals(KV kv) {
			return this.key.equals(kv.key);
		}

		public String wrap() {
			return key + op + "'" + value + "'";
		}
	}

	public static class Keys {
		private HashMap<KV, String> keys = new HashMap<>();

		/**
		 * 如果后面无则join_str=""
		 * 
		 * @param k
		 * @param join_str
		 */
		public void put(KV k, String join_str) {
			keys.put(k, join_str);
		}

		public String toString() {
			StringBuilder str = new StringBuilder();
			keys.entrySet().forEach(e -> {
				str.append(e.getKey());
				str.append(" ");
				str.append(e.getValue());
				str.append(" ");
			});

			return str.toString();
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

	public static void main(String[] args) throws IOException, SQLException {
		DBUtil.getConnection();
		String[] fails=DBUtil.createTablesFrom(FileUtil.projectFileAbPath("pms/sql/tables.sql", DBUtil.class));
		System.out.println(fails.length);
		// for (int i=0;i<100;++i) {
		// Stu stu=new Stu();
		// stu.setId(String.valueOf(RandomUtil.randomRangeOf(1000, 10000)));
		// stu.setNum(String.valueOf(i));
		// DBUtil.insertObject(stu, Stu.class,"stu");
		// }
		String json = JSON.toJSONString(DBUtil.toArrayList(DBUtil.page("stu", "id", 1, 12), Stu.class));
		System.out.println(json);
	}
}
