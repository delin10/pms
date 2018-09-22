package pms.util.db.mapper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import pms.util.db.SQLTable;
import pms.util.db.SQLTable.Attribute;
import pms.util.db.anno.Column;
import pms.util.db.anno.Table;
import pms.util.db.type.SQLType;
import pms.util.db.type.TypeDictionary;


public class Mapper {
	private Map<Class<?>,SQLTable> mapToType=new HashMap<>();
	public SQLTable map(Class<?> clazz,boolean refresh) {
		SQLTable sqltable=mapToType.get(clazz);
		if (sqltable!=null && !refresh) {
			return sqltable;
		}
		
		if (!clazz.isAnnotationPresent(Table.class)) {
			return null;
		}

		Table table = clazz.getAnnotation(Table.class);
		sqltable=new SQLTable(table.table());
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (!field.isAnnotationPresent(Column.class)) {
				continue;
			}
			
			Column column = field.getAnnotation(Column.class);
			TypeDictionary type_col = column.type();
			SQLType type=SQLType.create(type_col);
			int[] limit=column.limit();
			int limit_std=type_col.getLimit();
			int limit_set=limit.length;
			if (limit_std!=0&&limit_set==0) {
				System.err.println(clazz.getName()+"["+field.getName()+"]注释数据类型不合法");
				return null;
			}
			//mapToTable.put(field.getName(), name_col);
			int set=limit_std<limit_set?limit_std:limit_set;
			int[] limit_=new int[set];
			System.arraycopy(limit, 0, limit_, 0, set);
			String addition=Arrays.stream(limit_).mapToObj(i->String.valueOf(i)).collect(Collectors.joining(","));
			type.setAddtional(addition);
			Attribute attr=new Attribute(field.getName(),type);
			attr.setNot_null(column.not_null());
			attr.setPrimary_key(column.primary_key());
			if (column.foreign_key()) {
				attr.setForeign_key(column.refProp());
				attr.setRef_table(column.refTable());
			}
			sqltable.addAttribute(attr);
		}
		mapToType.put(clazz, sqltable);
		return sqltable;
	}
	
	public static void main(String...args) {
	}
}
