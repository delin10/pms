package pms.util.db.mapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import pms.util.StrUtil;
import pms.util.db.dict.WordDictionary;
import pms.util.regex.PatternUtil;

public class AttributeMapper {
	private WordDictionary dict=new WordDictionary();
	private String mod_bean_member="\tprivate %s %s;\r\n";
	
	public String mapToBeanDef(String attr) {
		return String.format(mod_bean_member,dict.mapToJavaType(attr), getName(attr));
	}
	
	public String[] split(String sql) {
		if (sql.isEmpty()) {
			return new String[0];
		}
		sql=StrUtil.replaceAllCaseIgnored(StrUtil.replaceAllCaseIgnored(sql, "\\s*,\\s*primary\\s*key[^,]*\\(.*\\)\\s*(?:,?)",""),"\\s*,\\s*foreign\\s*key\\s*\\(.*\\)\\s*references.*\\(.*\\)","");
		//System.out.println(sql);
		return StrUtil.split(sql,"[^\\d']\\s*,\\s*[^\\d']",1,1);
	}
	
	public String attrsBody(String sql) {
		int start_index=sql.indexOf("(");
		int end_index=sql.lastIndexOf(")");
		
		if (start_index<0||end_index<0||start_index>end_index) {
			return "";
		}
		return sql.substring(start_index+1, end_index).trim();
	}
	
	public String[] getName_Type(String attr) {
		int start=0;
		int end=0;
		
		attr=attr.trim();
		//System.out.println(attr);
		
		//System.out.println(attr);
		end=attr.indexOf(" ");
		String name=attr.substring(start,end);
		while(StrUtil.isEmpty(attr.charAt(end)))end++;
		String sub=attr.substring(end);
		String type=PatternUtil.getSubString(sub, "[\\w\\d_]*\\s*[\\(]?\\s*[\\d]*\\s*,?\\s*[\\d]*\\s*[\\)]?");
		return new String[] {name,type};
	}
	
	public String getName(String attr) {
		attr=attr.trim();
		//System.out.println(attr);
		int index=attr.indexOf(" ");
		if (index<0) {
			return "????";
		}
		
		String name=attr.substring(0, index);
		
		if ("primary".equalsIgnoreCase(name)||"foreign".equalsIgnoreCase(name)) {
			return "????";
		}
		return name;
	}
	
	public Map<String,String> attr_type(String sql){
		//System.out.println(sql);
		Map<String,String> map=new HashMap<>();
		Arrays.stream(split(attrsBody(sql))).forEach(attr->{
			String[] kv=getName_Type(attr);
			map.put(kv[0],kv[1]);
		});;
		
		return map;
		
	}
	
}
