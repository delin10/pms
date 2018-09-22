package pms.util;

public class ParamsChecker {
	public static boolean existsNullOrEmpty(Object... args) {
		if (args == null) {
			return false;
		}
		boolean res = true;
		for (Object o : args) {
			res = res && (o instanceof String?!((String)o).isEmpty():o!=null);
		}
		return res;
	}
	
	public static void main(String...args) {
		System.out.println(existsNullOrEmpty("a","a","a"));
	}
}
