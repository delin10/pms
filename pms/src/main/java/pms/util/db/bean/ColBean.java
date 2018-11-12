package pms.util.db.bean;

public class ColBean {
	private String col;
	private String alias;
	private boolean isblob;
	public String getCol() {
		return col;
	}
	public ColBean setCol(String col) {
		this.col = col;
		return this;
	}
	public String getAlias() {
		return alias;
	}
	public ColBean setAlias(String alias) {
		this.alias = alias;
		return this;
	}
	public boolean isBlob() {
		return isblob;
	}
	public ColBean setBlob(boolean isblob) {
		this.isblob = isblob;
		return this;
	}
}
