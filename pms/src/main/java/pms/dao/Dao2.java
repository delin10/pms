package pms.dao;


import java.util.Arrays;
import java.util.Map;

import pms.util.auth.manager.Session;
import pms.util.comm.Info;
import pms.util.db.DBUtil;
import pms.util.db.DBUtil.KV;
import pms.util.db.DBUtil.Keys;
import pms.util.db.DBUtil.SQL;

public class Dao2 {
	public static Info query(Session session, String type) {
		Info info = new Info();
		info.suc("success");
		if (type != null) {
			@SuppressWarnings("unchecked")
			Map<String, Object> _basic_ = (Map<String, Object>) session.getAttributes("_basic_");
			String community_name = (String) _basic_.get("community");
			System.out.println("community"+community_name);
			if (community_name == null) {
				info.setData(DBUtil.rsToMapList(DBUtil.queryAll(type, null), null));
			} else {
				SQL sql = SQL.create().select(new KV(type, "*").unwrap()).from(type);
				Keys keys = new Keys().start(new KV(type.equals("community")?"name":"community_name", community_name));
				sql.where(keys);
				info.setData(DBUtil.rsToMapList(DBUtil.query(sql.complete()), null));
			}
		} else {
			info.fail("fail");
		}
		return info;
	}

	public static Info add(Session session, Map<String, String> values, String type) {
		Info info = new Info();
		if (type != null && values != null) {
			if (DBUtil.insertMap(values, type)) {
				info.suc("success");
			}else {
				info.fail("fail");
			}
		} else {
			info.fail("fail");
		}
		return info;
	}

	public static Info update(Session session, Map<String, String> values, String type,Map<String, String>keys) {
		Info info = new Info();
		if (type != null) {
			Keys where=new Keys();
			keys.entrySet().forEach(key->{
					where.and(new KV(key.getKey(),key.getValue()));
			});
			if (DBUtil.updateByMap(values, type, where)) {
				info.suc("success");
			}else {
				info.fail("fail");
			}
		} else {
			info.fail("fail");
		}
		return info;
	}
	
	public static Info delete(Session session, String type,Map<String, String>[] keys) {
		Info info = new Info();

		if (type != null) {
			Arrays.stream(keys).forEach(map -> {
				Keys where=new Keys();
				map.entrySet().stream().map(entry->new KV(entry.getKey(),entry.getValue())).forEach(where::and);
				boolean res = DBUtil.keyDel(type,
						where);
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
}
