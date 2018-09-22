package pms.util.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pms.util.db.type.SQLType;

public class SQLTable {
	private String name;
	private ArrayList<Attribute> attrs=new ArrayList<>();
	
	public SQLTable(String table) {
		this.name=table;
	}

	public void addAttribute(Attribute attr) {
		attrs.add(attr);
	}
	
	public String createSQL() {
		Map<String, List<Attribute>> map=attrs.stream().filter(e->e.getRef_table()!=null).collect(Collectors.groupingBy(Attribute::getRef_table));
		ArrayList<String> foreign_keys=new ArrayList<>();
		map.entrySet().stream().filter(e->e.getKey()!=null).forEach(entry->{
			List<Attribute> attrs=entry.getValue();
			ArrayList<String> ref=new ArrayList<String>();
			ArrayList<String> refed=new ArrayList<String>();
			for (Attribute attr : attrs) {
				ref.add(attr.getName());
				refed.add(attr.getForeign_key());
			}
			
			foreign_keys.add(String.format("FOREIGN KEY (%s) REFERENCES %s(%s)", String.join(",",ref),entry.getKey(),String.join(",", refed)));
		});
		
		ArrayList<String> primary_keys=attrs.stream().filter(Attribute::isPrimary_key).map(Attribute::getName).collect(Collectors.toCollection(ArrayList<String>::new));
		
		
		return String.format("CREATE TABLE %s(%s%s%s)", name, attrs.stream().map(Attribute::toString).collect(Collectors.joining(",")),
				primary_keys.size()==0?"":String.format(",PRIMARY KEY(%s)", String.join(",", primary_keys)),
						foreign_keys.size()==0?"":","+String.join(",", foreign_keys));
	}
	
	public String toString() {
		return createSQL();
	}
	
	public static class Attribute{
		private String name;
		private SQLType type;
		private boolean not_null=false;
		private boolean primary_key=false;
		private String foreign_key=null;
		private String ref_table=null;
		public Attribute(String name,SQLType type) {
			this.type=type;
			this.name=name;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public SQLType getType() {
			return type;
		}
		public void setType(SQLType type) {
			this.type = type;
		}
		public boolean isNot_null() {
			return not_null;
		}
		public void setNot_null(boolean not_null) {
			this.not_null = not_null;
		}
		public boolean isPrimary_key() {
			return primary_key;
		}
		public void setPrimary_key(boolean primary_key) {
			this.primary_key = primary_key;
		}
		public String getForeign_key() {
			return foreign_key;
		}
		public void setForeign_key(String foreign_key) {
			this.foreign_key = foreign_key;
		}
		
		public String toString() {
			StringBuilder str=new StringBuilder();
			str.append(name);
			str.append(" ");
			str.append(type);
			str.append(not_null?"":" NOT NULL");
			
			return str.toString();
		}
		public String getRef_table() {
			return ref_table;
		}
		public void setRef_table(String ref_table) {
			this.ref_table = ref_table;
		}
		
	}
}
