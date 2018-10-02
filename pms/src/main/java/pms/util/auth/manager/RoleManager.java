package pms.util.auth.manager;

import pms.util.auth.Getter;
import pms.util.auth.bean.Role;
import pms.util.comm.Info;
import pms.util.db.DBUtil;
import pms.util.db.DBUtil.KV;
import pms.util.db.DBUtil.Keys;
import pms.util.db.DBUtil.SQL;
import pms.util.redis.cahce.Cache;

public class RoleManager {
	private String rel_table="roles";
	public final String SYS="0";
	public final String NORMAL="1";
	private static Cache cache=new Cache();
	public Info roles() {
		Info info=new Info();
		info.setData(cache.get("roles").all());
		info.suc("success");
		return info;
	}
	public Info auth(Session session, String user_id, String role_id) {
		Info info = new Info();
		// ��Ҫ�жϹ���ԱȨ��
		//System.out.println(session.getAttributes("role"));
		Role role = (Role) cache.get("roles").get(session.getAttributes("role").toString());
		if ("0".equals(role.getId()) && !"0".equals(user_id) && !"0".equals(role_id)) {
			boolean res=DBUtil.transaction()
			.begin()
			.add(SQL.create().delete("users_roles").where(new Keys().start(new KV("user_id",user_id))))
			.add(SQL.create().insert("users_roles").values(new KV("user_id",user_id),new KV("role_id",role_id)))
			.commit();
			if (res) {
				Getter.sm.getSessionOf(user_id).setAttributes("role", role_id);
				info.suc("�ɹ�������ɫ");
			}else {
				info.fail("������ɫʧ��[����ԭ��:���ݿ��������ع�]");
			}
		}else {
			info.fail("Ȩ�޲�����߳�������ϵͳ����ԱȨ��");
		}
		return info;
	}
	
	
	public Info addRole(Role role) {
		Info info=new Info();
		if(cache.get("roles").updateCache(role)) {
			info.suc("�ɹ���ӽ�ɫ");
		}else {
			info.fail("��ӽ�ɫʧ��");
		}
		return info;
	}
	
	public Info delRole(String role_id) {
		Info info=new Info();
		if(cache.get(rel_table).deleteCache(role_id)) {
			info.suc("�ɹ�ɾ��");
		}else {
			info.fail("ɾ��ʧ��");
		}
		return info;	
	}
	
	/**
	 * ��ȡ��ɫ����
	 * @param role_id
	 * @return ��Ϣ����
	 */
	public Info getRole(String role_id) {
		Info info=new Info();
		Object o=cache.get("roles").get(role_id);
		if (o==null) {
			info.fail("not founf!");
		}else {
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
		if (sys==null) {
			Role role=new Role();
			role.setId(SYS);
			role.setDescription("ϵͳ����Ա");
			role.setAvailable("1");
			role.setName("sys");
			cache.get(rel_table).updateCache(role);
		}
		
		if (normal==null) {
			Role role=new Role();
			role.setId(NORMAL);
			role.setDescription("��ͨ�û�");
			role.setAvailable("1");
			role.setName("normal");
			cache.get(rel_table).updateCache(role);
		}
	}
	
}
