package pms.util.test;

public class TestTime {
	public static void test(String flag,Test test) {
		long start=System.currentTimeMillis();
		test.test();
		long end=System.currentTimeMillis();
		System.out.println(flag+"耗费:"+(end-start)+"ms");	
	}
}
