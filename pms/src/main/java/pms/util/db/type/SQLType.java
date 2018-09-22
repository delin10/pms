package pms.util.db.type;

public class SQLType {
	private	final String name;
	private final int limit;
	private String additional;
	
	private SQLType(String name,int limit) {
		this.name=name;
		this.limit=limit;
	}
	
	public static SQLType create(TypeDictionary type) {
		return new SQLType(type.getValue(),type.getLimit());
	}

	public void setAddtional(String additional) {
		this.additional = String.format("(%s)", additional);
	}
	
	public String toString() {
		return name+(limit==0?"":additional);
	}
}
