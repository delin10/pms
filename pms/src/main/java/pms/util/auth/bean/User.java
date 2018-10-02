package pms.util.auth.bean;

import java.io.Serializable;

import pms.util.reflect.anno.Skip;

public class User implements Serializable{
	@Skip(skip=true)
	private static final long serialVersionUID = -7697007125728613034L;
	private String id;
	private String rel_id;
	private String pwd;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getRel_id() {
		return rel_id;
	}
	public void setRel_id(String rel_id) {
		this.rel_id = rel_id;
	}
}
