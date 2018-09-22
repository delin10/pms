package pms.util.db.parser;

import java.util.Arrays;
import java.util.Map;

import pms.util.PatternUtil;
import pms.util.StrUtil;
import pms.util.db.mapper.AttributeMapper;

public class SQLTableParserImpl implements Parser {
	private String table;
	private Map<String,String> attrs;
	private AttributeMapper mapper=new AttributeMapper();
	private String mod_bean="public class %s{\r\n"+
							"%s"
							+"}";


	@Override
	public void parse(String content) {
		this.table=StrUtil.firstLetterToUpperCase(PatternUtil.getSubString(content, "create\\s*table\\s*([\\w\\d_]*?)\\s*\\("));
		//System.out.println(table);
		attrs=mapper.attr_type(content);
	}
	
	public String toBean(String sql) {
		StringBuilder bean=new StringBuilder();
		String body=mapper.attrsBody(sql);
		String[] attrs=mapper.split(body);
		if (attrs.length==0) {
			return "";
		}
		
		Arrays.stream(attrs).map(str->str.trim()).forEach(attr->{
			bean.append(mapper.mapToBeanDef(attr));
		});
		
		//System.out.println();
		
		return String.format(mod_bean,table,bean.toString());
	}

	public String getTable() {
		return table;
	}

	public Map<String,String> getAttrs() {
		return attrs;
	}
}
