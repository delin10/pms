package pms.dao;

import java.util.ArrayList;
import java.util.HashMap;

import pms.util.auth.Getter;
import pms.util.auth.bean.Role;
import pms.util.auth.manager.Session;
import pms.util.comm.Info;
import pms.util.comm.KV_Obj;
import pms.util.db.DBUtil;
import pms.util.db.DBUtil.KV;
import pms.util.db.DBUtil.Keys;
import pms.util.db.DBUtil.SQL;
import pms.view.UserView;

public class Dao {
	public static Getter get = new Getter();

	public static Info setRole(Session session, String user_id, String role_id) {
		return Getter.rolem.auth(session, user_id, role_id);
	}

	public static Info auth(Session session, ArrayList<String> resource_ids) {
		Info info = new Info();
		ArrayList<Info> failed_infos = new ArrayList<Info>();
		resource_ids.stream().forEach(id -> {
			Info info_temp = new Info();
			info_temp = Getter.am.auth(session, id);
			info_temp.setInfo("id:" + id + "\r\n" + info_temp.getInfo());
			failed_infos.add(info_temp);
		});
		info.suc("");
		info.setData(failed_infos);
		return info;
	}

	public static Info login(String id, String pwd) {
		return Getter.um.login(id, pwd, null);
	}

	public static Info resources_tree(Session session) {
		Info info = new Info();
		KV_Obj kv = KV_Obj.create().setAttr("url").setComparator((a, b) -> {
			return a.equals(b) ? 0 : -1;
		}).setSrc("");

		info.setData(Getter.um.resources_where(session.getAttributes("role").toString(), kv));
		info.suc("sucess");
		return info;
	}

	public static Info showUsers() {
		Info info = new Info();
		ArrayList<Object> ls = DBUtil
				.toArrayList(
						DBUtil.query((SQL.create().select(UserView.class).from("users").left_join("users_roles")
								.on(new Keys().start(new KV("users.id", "user_id").unwrap())).left_join("roles")
								.on(new Keys().start(new KV("roles.id", "role_id").unwrap()))).complete()),
						UserView.class);
		info.setData(ls);
		info.suc("success");
		return info;
	}

	public static Info roles() {
		return Getter.rolem.roles();
	}

	public static Info resources_tab(Session session, String fid) {
		Info info = new Info();
		KV_Obj kv_a = KV_Obj.create().setAttr("url").setComparator((a, b) -> {
			return a.equals(b) ? -1 : 0;
		}).setSrc("");

		KV_Obj kv_b = KV_Obj.create().setAttr("fid").setComparator((a, b) -> {
			return a.equals(b) ? 0 : -1;
		}).setSrc(fid);

		info.setData(Getter.um.resources_where(session.getAttributes("role").toString(), kv_a, kv_b));
		info.suc("sucess");
		return info;
	}

	public static Info resources(Session session) {
		Info info = new Info();
		HashMap<String, Object> data = new HashMap<>();
		data.put("resources", session.getAttributes("resources"));
		data.put("role", session.getAttributes("role"));
		info.setData(data);
		info.suc("success");
		return info;
	}
}
