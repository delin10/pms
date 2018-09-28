package pms.bean;

public class Users{
	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRel_id() {
		return rel_id;
	}
	public void setRel_id(String rel_id) {
		this.rel_id = rel_id;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	private String rel_id;
	private String pwd;
}