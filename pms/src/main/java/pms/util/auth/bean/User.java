package pms.util.auth.bean;

import java.io.Serializable;

import pms.util.reflect.anno.Skip;

public class User implements Serializable{
	@Skip(skip=true)
	private static final long serialVersionUID = -7697007125728613034L;
	private String id;
	private String owner_id;
	private String pwd;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOwner_id() {
		return owner_id;
	}
	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
}
