package pms.util.db.constants;

public enum JOINTYPE {
	NATURAL("natural join",0),LEFT("left join",1),RIGHT("right join",2),ALL("join",3),NONE("",4);
	private String value;
	private int index;
	
	private JOINTYPE(String value,int index) {
		this.value=value;
		this.index=index;
	}
	public String getValue() {
		return value;
	}

	public int getIndex() {
		return index;
	}
	
	public String toString() {
		return this.getValue();
	}
}
