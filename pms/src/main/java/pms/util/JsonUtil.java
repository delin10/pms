package pms.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtil {
	public static String mapToJSON(Map<String,String> map) {
		StringBuilder json=new StringBuilder("{}");
		String item="\"%s\":\"%s\"";
		map.entrySet().forEach(entry->{
			int pos=json.length()-1;
			json.insert(pos, String.format(item,entry.getKey(),entry.getValue())+",");
		});
		int size=json.length();
		return json.replace(size-2, size-1, "").toString();
	}
	
	public static String[] getAttributesByName(String json , String name) {
		Pattern pattern=Pattern.compile(String.format("\"%s\"\\:\"(.*?)\"",name));
		Matcher matcher=pattern.matcher(json);

		ArrayList<String> res=new ArrayList<>();
		System.out.println(json);
		while (matcher.find()) {
			/**
			 * ƥ���group(0)��ʾ����ƥ��Ĵ� 
			 * group(1)��ʾ�����е�һ�������б�ʾ������ƥ��ֵ 
			 * group(2)��ʾ�����еڶ��������б�ʾ������ƥ��ֵ
			 */
			res.add(matcher.group(1));
		}
		
		if (res.size()==0)res.add("");
		
		return res.toArray(new String[]{});
	}
	
	public static Map<String,String> jsonToMap(String json) {
		Map<String,String> map=new HashMap<>();
		
		Pattern regex=Pattern.compile("(\".*?\"\\:.*?)[,\\}]");
		Matcher matcher=regex.matcher(json);
		while(matcher.find()) {
			String entry=matcher.group(1);
			String[] kv=entry.split(":");
			String key=kv[0].substring(1, kv[0].length()-1);
			if (kv.length<2) {
				map.put(key, "");
			}else {
				map.put(key,kv[1].charAt(0)=='"'?kv[1].substring(1, kv[1].length()-1):kv[1]);
			}
		}
		return map;
	}
	
	public static void main(String[] args) {
		String json="{\"phoneNumber\":,\"identiCode\":\"170762\",\"password\":\"YiITiz6yNiulQF/NKCZJPkoq7FtocuatuHUwm4S12UPvTo7/Zk5rrGSifOtgUeesXZGJhN0JSgAODpUAbSBd2pNa5dWTeKDhBuIj07g7r+O/324vFXNQSwN/rTs5gixNMZ6fpdNsFLG50ql02YrQq6fFu4UA/uIYOYYfVkUr/mOT/NuppWXU7KEVSjD8vEPNkzY1ZGdrb2yrnxmGliHxM48E0fQnK/gHVn2Nz1SCCKgpDRXqMMcrciRdJZOuMcK4ts+qcHi3cFfPeJ4VjD3UAQlavQDhF4/wj4IVARbGU7bOCaILN/TMzY9O3/eSI3mfjxkCfb+ENk/wmDLSyvbC5Q==\",\"timestamp\":1536310650000}\r\n" + 
				"";
		
		String[] strArray=JsonUtil.getAttributesByName(json, "1");
		Arrays.stream(strArray).forEach(System.out::println);
		System.out.println(jsonToMap(json));
	}
}
