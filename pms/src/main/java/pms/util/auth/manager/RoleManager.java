package pms.util.auth.manager;

import java.util.ArrayList;

import pms.util.auth.Getter;
import pms.util.auth.bean.Role;
import pms.util.comm.Info;
import pms.util.db.DBUtil;
import pms.util.db.DBUtil.KV;
import pms.util.db.DBUtil.Keys;
import pms.util.db.DBUtil.SQL;
import pms.util.redis.cahce.Cache;
import pms.util.redis.cahce.Cache.CacheSwap;

public class RoleManager {
	public static String rel_table = "roles";
	public static final String SYS = "0";
	public static final String NORMAL = "1";
	private static Cache cache = new Cache();

	public Info roles() {
		Info info = new Info();
		info.setData(cache.get("roles").all());
		info.suc("success");
		return info;
	}

	public Info auth(String user_id, String role_id) {
		Info info = new Info();
		if (!SYS.equals(user_id) && !SYS.equals(role_id)) {
			boolean res = DBUtil.transaction().begin()
					.add(SQL.create().delete("users_roles").where(new Keys().start(new KV("user_id", user_id))))
					.add(SQL.create().insert("users_roles").values(new KV("user_id", user_id),
							new KV("role_id", role_id)))
					.commit();
			if (res) {
				Session session = Getter.sm.getSessionOf(user_id);
				if (session != null) {
					session.setAttributes("role", role_id);
				}
				info.suc("�ɹ�������ɫ");
			} else {
				info.fail("������ɫʧ��[����ԭ��:���ݿ��������ع�]");
			}
		} else {
			info.fail("Ȩ�޲�����߳�������ϵͳ����ԱȨ��");
		}
		return info;
	}

	public Info updateRole(Role role) {
		Info info = new Info();
		boolean res = cache.get("roles").updateCache(role);
		if (res) {
			info.suc("�ɹ�����");
		} else {
			info.fail("����ʧ��");
		}
		return info;
	}

	public Info delRole(String role_id) {
		Info info = new Info();
		if (SYS.equals(role_id) || NORMAL.equals(role_id)) {
			info.fail("����ɾ���ڶ���ɫ-[����Ա����ͨ�û�]");
			;
		} else {
			ArrayList<String> user_ids = DBUtil.toArrayListOf("user_id",
					DBUtil.keyQuery("users_roles", new KV("role_id", role_id)));
			if (user_ids.size() == 0) {
				info.suc("�ý�ɫ�޹����û�");
				;
			} else {
				user_ids.forEach(user_id -> {
					info.wrapInfo(auth(user_id, role_id).append(String.format(",�ý�ɫ����%s���û�", user_ids.size())));
				});
			}

			info.line();
			boolean res = DBUtil.keyDel("roles_resources", new KV("role_id", role_id));
			if (res) {
				info.append("�ɹ�ɾ��������Դ");
			} else {
				info.append("ɾ����ɫ������Դ�����쳣");
			}
			boolean del_role_res = cache.get("roles").deleteCache(role_id);
			info.line();
			if (del_role_res) {
				info.append("�ɹ�ɾ����ɫ");
			} else {
				info.append("ɾ����ɫ�쳣");
			}
		}
		return info;
	}

	public Info addRole(Role role) {
		Info info = new Info();
		if (cache.get("roles").updateCache(role)) {
			info.suc("�ɹ���ӽ�ɫ");
		} else {
			info.fail("��ӽ�ɫʧ��");
		}
		return info;
	}

	/**
	 * ��ȡ��ɫ����
	 * 
	 * @param role_id
	 * @return ��Ϣ����
	 */
	public Info getRole(String role_id) {
		Info info = new Info();
		Object o = cache.get("roles").get(role_id);
		if (o == null) {
			info.fail("not founf!");
		} else {
			info.suc("found!");
			info.setData(o);
		}
		return info;
	}

	/**
	 * ����ĳ����ɫ
	 * 
	 * @param role_id
	 * @return
	 */
	public Info forbidden(Role role) {
		Info info = new Info();
		if (SYS.equals(role.getId())) {
			info.fail("dont be allowed to forbidden sys Manager");
			return info;
		}

		role.setAvailable("0");
		cache.get("roles").updateCache(role);
		info.suc("succeed to forbidden!");
		return info;
	}

	/**
	 * �����ɫ
	 * 
	 * @param role_id
	 * @return
	 */
	public Info permit(Role role) {
		Info info = new Info();
		if (SYS.equals(role.getId())) {
			info.fail("dont be allowed to forbidden sys Manager");
			return info;
		}

		role.setAvailable("1");
		cache.get("roles").updateCache(role);
		info.suc("succeed to permit!");
		return info;
	}

	public void init() {
		Object sys = cache.get(rel_table).get(SYS);
		Object normal = cache.get(rel_table).get(NORMAL);
		CacheSwap swap = cache.get(rel_table);
		if (sys == null) {
			Role role = new Role();
			role.setId(SYS);
			role.setDescription("ϵͳ����Ա");
			role.setAvailable("1");
			role.setName("sys");
			swap.updateCache(role);
		}

		if (normal == null) {
			Role role = new Role();
			role.setId(NORMAL);
			role.setDescription("��ͨ�û�");
			role.setAvailable("1");
			role.setName("normal");
			swap.updateCache(role);
		}
		swap.sync();
	}

	public String generateId() {
		return cache.get("roles").incMaxId();
	}

}
