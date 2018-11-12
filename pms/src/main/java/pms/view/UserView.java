package pms.view;

import pms.util.db.anno.Col;
import pms.util.db.anno.SQLQuery;
@SQLQuery(sql="select roles.name as rolename,roles.id as roleid,users.id as userid,roles.description as roledescription from users left join users_roles on users.id=user_id left join roles on roles.id=role_id ")
public class UserView {
	@Col(col="users.id",alias="userid")
	private String userid;
	@Col(col="roles.id",alias="roleid")
	private String roleid;
	@Col(col="roles.name",alias="role_name")
	private String role_name;
	@Col(col="roles.description",alias="role_description")
	private String role_description;
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getRoleid() {
		return roleid;
	}
	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}
	public String getRole_name() {
		return role_name;
	}
	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}
	public String getRole_description() {
		return role_description;
	}
	public void setRole_description(String role_description) {
		this.role_description = role_description;
	}
	
}
