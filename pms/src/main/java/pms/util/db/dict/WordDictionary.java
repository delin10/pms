package pms.util.db.dict;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import pms.util.regex.PatternUtil;

public class WordDictionary {
	private Map<String,String> type_dict=new HashMap<>(1024);
	{
		type_dict.put("VARCHAR\\s*[\\(]\\s*[0-9]*\\s*[\\)]", "String");
		type_dict.put("CHAR", "String");
		type_dict.put("TEXT", "String");
		type_dict.put("CLOB", "String");
		type_dict.put("BLOB", "InputStream");
		type_dict.put("DATETIME", "String");
		type_dict.put("TIMESTAMP", "String");
		type_dict.put("INT", "String");
		type_dict.put("INT UNSIGNED", "String");
		//type_dict.put("NUMBER", "String");
		type_dict.put("DECIMA\\s*\\(\\s*[1-9]+?\\s*,\\s*[0-9]*?\\s*\\)", "double");
		type_dict.put("DECIMAL\\(\\s*[1-9]+?\\s*\\)", "int");
		type_dict.put("NUMBER\\s*\\(\\s*[1-9]+?\\s*\\)", "int");
		type_dict.put("NUMBER\\s*\\(\\s*[1-9]+?\\s*,\\s*[0-9]*?\\s*\\)", "double");
		type_dict.put("FLOAT", "String");
		type_dict.put("LONG", "long");
	}
	
	public String mapToJavaType(String attr_Sql) {
		Optional<String> res=type_dict.entrySet().stream()
				.filter(e->!PatternUtil.match(e.getKey(), attr_Sql).isEmpty())
				.map(kv->kv.getValue()).reduce((acc,en)->acc=en);
		if (res.isPresent()) {
			return res.get();
		}
		return "???????";
	}
}
