package pms.service.impl;

import pms.service.BasicService;
import pms.util.auth.manager.Session;
import pms.util.comm.Info;
import pms.util.db.DBUtil.KV;
import pms.util.db.DBUtil.Keys;
import pms.util.db.DBUtil.SQL;
import pms.view.UserView;

public class UserService extends BasicService {
	@Override
	public Info query(Session session) {
		SQL sql=SQL.create().select(UserView.class).from("users").left_join("users_roles").on(new Keys().start(new KV("users.id","user_id").unwrap())).left_join("roles").on(new Keys().start(new KV("roles.id","role_id").unwrap()));
		System.out.println(sql);
		return dao.customQuery(sql);
	}

	@Override
	public String getTable() {return null;}
	@Override
	public String getCommunityColumn() {return null;}
	@Override
	public boolean enableDelete() {return false;}
	@Override
	public boolean enableAdd() {return false;}
	@Override
	public boolean enableUpdate() {return false;}
}
