package pms.util.db.dict;

public enum DBOP {
	SHOW_OWN_TABLE("show tables user created"),
	PAGE("select data by page"),;
	private String value;
	
	private DBOP(String value) {
		this.value=value;
	}

	public String getValue() {
		return value;
	}	
	
	public String toString() {
		return value;
	}
}
