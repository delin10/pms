package pms.util.db;

import java.util.HashMap;
import java.util.stream.Stream;

import pms.util.db.constants.Comparator;

public class UpdatePacker {
	private static String sql = "update %s set %s where %s";
	private String tableName = null;
	private HashMap<String, Integer> intParam = new HashMap<>();
	private HashMap<String, String> notIntParam = new HashMap<>();
	private StringBuilder whereCon = new StringBuilder();

	public UpdatePacker and(String name, Object value,Comparator comparator) {
		if (whereCon.length() != 0) {
			whereCon.append('&');
		}
		whereCon.append(name);
		whereCon.append(comparator);
		whereCon.append(processValue(value));
		return this;
	}
	
	public UpdatePacker or(String name, Object value,Comparator comparator) {
		if (whereCon.length() != 0) {
			whereCon.append('&');
		}
		whereCon.append(name);
		whereCon.append(comparator);
		whereCon.append(processValue(value));
		return this;
	}
	
	public UpdatePacker not() {
		if (whereCon.length()==0) {
			return this;
		}
		whereCon.insert(0, "!(");
		whereCon.insert(whereCon.length(), ")");
		return this;
	}
	

	public String getTableName() {
		return tableName;
	}

	public UpdatePacker setTableName(String tableName) {
		this.tableName = tableName;
		return this;
	}

	public int getIntParam(String name) {
		return intParam.get(name);
	}

	public UpdatePacker setIntParam(String name, int value) {
		intParam.put(name, value);
		return this;
	}

	public String getNotIntParam(String name) {
		return notIntParam.get(name);
	}

	public UpdatePacker setNotIntParam(String name, String value) {
		notIntParam.put(name, value);
		return this;
	}

	public String getWhereCon() {
		return whereCon.toString();
	}
	
	public String toString() {
		if (whereCon.length()==0||intParam.size()==0||notIntParam.size()==0||tableName==null) {
			return null;
		}
		String[] params=Stream.of(intParam.entrySet().stream().map(entry->String.format("%s=%s", entry.getKey(),entry.getValue())).toArray(String[]::new)
					,notIntParam.entrySet().stream().map(entry->String.format("%s='%s'", entry.getKey(),entry.getValue())).toArray(String[]::new))
				.flatMap(s->Stream.of(s))
					.toArray(String[]::new);
			  
		String paramsStr=String.join(",", params);
		return String.format(sql, tableName,paramsStr,whereCon.toString());
	}
	
	private String processValue(Object o) {
		String vf=o instanceof Integer?"%s":"'%s'";
		return String.format(vf, o);
	}
	
	public static void main(String[]args) {
		UpdatePacker update=new UpdatePacker();
		update.setTableName("my")
			  .setIntParam("test", 100)
			  .setNotIntParam("rt", "poo")
			  .setIntParam("j", 11)
			  .not()
			  .and("str", "n", Comparator.SMALLER)
			  .and("p", "aa", Comparator.LARGER)
			  .or("er", "aa",Comparator.NOTEQUALS)
			  .not();
		System.out.println(update.toString());
	}
}
