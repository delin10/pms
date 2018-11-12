package pms.dao;

import java.util.Arrays;
import java.util.Map;

import pms.util.auth.manager.Session;
import pms.util.comm.Info;
import pms.util.db.DBUtil;
import pms.util.db.DBUtil.KV;
import pms.util.db.DBUtil.Keys;
import pms.util.db.DBUtil.SQL;

public class SimpleDao {
	public Info query(String table, Keys keys) {
		Info info = new Info();
		info.suc("success");
		if (table != null) {
			SQL sql = SQL.create().select(new KV(table, "*").unwrap()).from(table);
			if (keys != null) {
				sql.where(keys);
			}
			info.setData(DBUtil.rsToMapList(DBUtil.query(sql.complete()), null));
		} else {
			info.fail("fail");
		}
		return info;
	}

	public Info add(Map<String, String> values, String table) {
		Info info = new Info();
		if (table != null && values != null) {
			if (DBUtil.insertMap(values, table)) {
				info.suc("success");
			} else {
				info.fail("fail");
			}
		} else {
			info.fail("fail");
		}
		return info;
	}

	public Info update(Map<String, String> values, String table, Map<String, String> keys) {
		Info info = new Info();
		if (table != null) {
			Keys where = new Keys();
			keys.entrySet().forEach(key -> {
				where.and(new KV(key.getKey(), key.getValue()));
			});
			if (DBUtil.updateByMap(values, table, where)) {
				info.suc("success");
			} else {
				info.fail("fail");
			}
		} else {
			info.fail("fail");
		}
		return info;
	}

	public Info delete(Session session, String table, Map<String, String>[] keys) {
		Info info = new Info();

		if (table != null) {
			Arrays.stream(keys).forEach(map -> {
				Keys where = new Keys();
				map.entrySet().stream().map(entry -> new KV(entry.getKey(), entry.getValue())).forEach(where::and);
				boolean res = DBUtil.keyDel(table, where);
				if (res) {
					info.append(map + "  success;");
				} else {
					info.append(map + "  failed;");
				}
			});
		} else {
			info.fail("fail");
		}
		return info;
	}
	
	public Info customQuery(SQL sql) {
		Info info=new Info();
		info.setData(DBUtil.rsToMapList(DBUtil.query(sql.complete()), null));
		info.suc("success");
		return info;
	}
}
