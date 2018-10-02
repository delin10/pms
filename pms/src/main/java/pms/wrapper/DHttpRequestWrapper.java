package pms.wrapper;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class DHttpRequestWrapper extends HttpServletRequestWrapper {
	private Map<Object, Object> attrs = new HashMap<>();

	public DHttpRequestWrapper(HttpServletRequest request) {
		super(request);
		addAttrs(request);
	}


	public void addAttrs(HttpServletRequest request) {
		Enumeration<String> enumeration=request.getAttributeNames();
		while(enumeration.hasMoreElements()) {
			String key=enumeration.nextElement();
			attrs.put(key, request.getAttribute(key));
		}
	}
	
	public Object getAttribute(String key) {
		return attrs.get(key);
	}
}
