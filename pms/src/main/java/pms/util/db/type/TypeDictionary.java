package pms.util.db.type;

public enum TypeDictionary {
	VARCHAR("VARCHAR",1),
	CHAR("CHAR",1),
	INT("INT",0),
	UNSIGNED_INT("INT UNSIGNED",0),
	TEXT("TEXT",0),
	CLOB("CLOB",0),
	FLOAT("FLOAT",0),
	LONG("LONG",0),
	DOUBLE("DOUBLE",0),
	DECIMAL("DECIMAL",2),
	NUMBER("NUMBER",2),
	DATETIME("DATETIME",0),
	TIMESTAMP("TIMESTAMP",0),
	BLOB("BLOB",0);
	private String value;
	private int limit;
	
	private TypeDictionary(String value,int limit) {
		this.value=value;
		this.limit=limit;
	}

	public int getLimit() {
		return limit;
	}

	public String getValue() {
		return value;
	}
}
