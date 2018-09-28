package pms.util.url;

import java.util.HashSet;
import java.util.Iterator;

public class URLParser {
	public static DURL parse(String url) {
		int body_end = url.indexOf("?");
		if (body_end < 0) {
			return new DURL(url);
		}
		DURL u = new DURL(url.substring(0, body_end));
		int size = url.length();
		int param_start = body_end + 1;
		StringBuilder param = new StringBuilder();
		while (param_start < size) {
			char ch = url.charAt(param_start++);
			if (ch != '=') {
				param.append(ch);
			}else {
				u.add(param.toString());
				param=new StringBuilder();
				while(param_start<size&&url.charAt(param_start++)!='&');
				
			}
		}
		return u;
	}

	public static class DURL {
		private String url;
		private HashSet<String> params = new HashSet<>();

		DURL(String url) {
			this.url = url;
		}

		void add(String param) {
			params.add(param.toLowerCase());
		}

		public boolean equals(String uri, HashSet<String> params) {
			if (this.url.equals(uri)) {
				if (this.params.size() > this.params.size()) {
					return false;
				}

				return this.params.stream().map(params::contains).reduce((acc, next) -> {
					return acc = acc & next;
				}).get();
			}
			return false;
		}
		
		public String toString() {
			StringBuilder str=new StringBuilder();
			Iterator<String> it=params.iterator();
			int pos=0;
			if (it.hasNext()) {
				str.append(it.next());
				str.append("={");
				str.append(pos++);
				str.append("}");
			}
			while(it.hasNext()) {
				str.append("&");	
				str.append(it.next());
				str.append("={");
				str.append(pos++);
				str.append("}");
			}
			return url+"?"+str.toString();
		}
	}
	
}
