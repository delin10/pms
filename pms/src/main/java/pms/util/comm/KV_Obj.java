package pms.util.comm;

import java.util.Comparator;

public class KV_Obj {
	private Object src;
	private String attr;
	@SuppressWarnings({ "rawtypes"})
	private Comparator comparator;

	public static KV_Obj create() {
		return new KV_Obj();
	}
	public KV_Obj setComparator(@SuppressWarnings("rawtypes") Comparator comparator) {
		this.comparator = comparator;
		return this;
	}
	

	public Object getSrc() {
		return src;
	}

	public KV_Obj setSrc(Object src) {
		this.src = src;
		return this;
	}

	public String getAttr() {
		return attr;
	}

	public KV_Obj setAttr(String attr) {
		this.attr = attr;
		return this;
	}

	@SuppressWarnings("unchecked")
	public int compare(Object o) {
		return comparator.compare(src, o);
	}

}
