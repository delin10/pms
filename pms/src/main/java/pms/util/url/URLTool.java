package pms.util.url;

import java.util.Enumeration;
import java.util.HashSet;

public class URLTool {
	public boolean isEquals(String url,Enumeration<String> params,String target_url) {
		HashSet<String> set=new HashSet<>();
		while(params.hasMoreElements()) {
			set.add(params.nextElement());
		}
		
		return  URLParser.parse(target_url).equals(url, set);
	}
}
