package pms.util.test;

public class DLoop {
	public static void loop(int begin,int end,int step,Loop loop) {
		for (int i=begin;i<end;i+=step) {
			loop.loop(i);
		}
	}
}
