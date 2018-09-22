package pms.util.db;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import pms.util.db.constants.JOINTYPE;

public class SelectPacker implements Packer {
	private static String sql = "select %s from %s";
	private HashMap<String,Table> tables=new HashMap<>(); 
	private Table now=new Table();
	private Table next=new Table();
	
	/**
	 * 添加需要查询的列
	 * *表示所有列，但是之后无法添加其它col 
	 * @param col
	 */
	public SelectPacker addTable(String table) {
		now.name=table;
		next=now;
		return this;
	}
	
	public SelectPacker addCol(String col) {
		next.cols.add(col);
		
		return this;
	}
	
	public SelectPacker naturalJoin(String table) {
		next.type=JOINTYPE.NATURAL;
		Table prev=next;
		next();
		prev.to=next;
		next.name=table;
		return this;
	}

	@Override
	public ResultSet exec() {
		
		return null;
	}
	
	private  SelectPacker next() {
		next=new Table();
		return this;
	}
	
	public  SelectPacker complete() {
		tables.put(now.name, now);
		now=new Table();
		next=now;
		return this;
	}
	
	private class Table{
		String name="";
		JOINTYPE type=JOINTYPE.NONE;
		Table to=null;
		ArrayList<String> cols=new ArrayList<>();
	}

}
