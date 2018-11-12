package pms.service;


import java.util.Arrays;
import java.util.Map;

import pms.dao.SimpleDao;
import pms.util.auth.manager.RoleManager;
import pms.util.auth.manager.Session;
import pms.util.comm.Info;
import pms.util.db.DBUtil;
import pms.util.db.DBUtil.KV;
import pms.util.db.DBUtil.Keys;
//
//
//把Info分成Web Info和Client Info
//
//

/**
 * 我们把增删查改的url设计成action表示操作, json中包含增、删、改的内容 对于查, 采用判断角色访问资源的方式
 * 
 * @author delin
 *
 */
public abstract class BasicService {
	protected static SimpleDao dao = new SimpleDao();

	public Info query(Session session) {
		if (!enableQuery()) {
			Info info=new Info();
			info.fail("not support service");
			return info;
		}
		String role_id = (String) session.getAttributes("role");
		if (RoleManager.SYS.equals(role_id)) {
			return dao.query(getTable(), null);
		} else {
			try {
				Keys keys = keys(session);
				return dao.query(getTable(), keys);
			} catch (IllegalArgumentException e) {
				// TODO: handle exception
				return fail();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Info add(Session session, Object values) {
		Info info = fail();
		if (!enableAdd()) {
			info.setInfo("not suport service");
			return info;
		}
		if (getTable() != null && values != null) {
			if (DBUtil.insertMap((Map<String, String>)values, getTable())) {
				info.suc("success");
			}
		}
		return info;
	}

	@SuppressWarnings("unchecked")
	public Info update(Session session, Object values, Map<String, String> keys) {
		Info info = fail();
		if (!enableUpdate()) {
			info.setInfo("not suport service");
			return info;
		}
		if (getTable() != null) {
			Keys where = new Keys();
			keys.entrySet().forEach(key -> {
				where.and(new KV(key.getKey(), key.getValue()));
			});
			if (DBUtil.updateByMap((Map<String, String>) values, getTable(), where)) {
				info.suc("success");
			}
		}
		return info;
	}

	@SuppressWarnings("unchecked")
	public Info delete(Session session, Object keys) {
		Info info = new Info();
		if (!enableDelete()) {
			info.setInfo("not suport service");
			return info;
		}
		if (getTable() != null) {
			Arrays.stream((Map<String, String>[])keys).forEach(map -> {
				Keys where = new Keys();
				map.entrySet().stream().map(entry -> new KV(entry.getKey(), entry.getValue())).forEach(where::and);
				boolean res = DBUtil.keyDel(getTable(), where);
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

	protected Info fail() {
		Info info = new Info();
		info.fail("fail");
		return info;
	}

	public Keys keys(Session session) {
		@SuppressWarnings("unchecked")
		Map<String, Object> _basic_ = (Map<String, Object>) session.getAttributes("_basic_");
		String community_name = (String) _basic_.get("community");
		System.out.println("community" + community_name);
		if (community_name == null) {
			throw new IllegalArgumentException("community_name is null");
		}
		return new Keys().start(new KV(getCommunityColumn(), community_name));
	}
	
	public boolean enableQuery() {
		return true;
	}
	
	public boolean enableUpdate() {
		return true;
	}
	
	public boolean enableDelete() {
		return true;
	}
	
	public boolean enableAdd() {
		return true;
	}
	
	public abstract String getTable();

	public abstract String getCommunityColumn();
	
}
