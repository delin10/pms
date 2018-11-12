package pms;

import pms.comm.RuntimeStorage;
import pms.util.test.DLoop;
import pms.util.test.TestTime;

public class TestRuntimeStorage {
	public static void main(String[] args) {
		//度过加载类和初始化类的时间的时间
		TestTime.test("嵌套条件消耗时间", ()->{
			DLoop.loop(0, 100, 1, i->{
				RuntimeStorage.getBuildingService();
			});
		});
		
		TestTime.test("嵌套条件消耗时间", ()->{
			DLoop.loop(0, 1000000, 1, i->{
				RuntimeStorage.getBuildingService();
			});
		});
		
		TestTime.test("非嵌套条件消耗时间", ()->{
			DLoop.loop(0, 1000000, 1, i->{
				RuntimeStorage.getBuildingService();
			});
		});
		
		TestTime.test("非嵌套条件消耗时间", ()->{
			DLoop.loop(0, 1000000, 1, i->{
			});
		});
		
		TestTime.test("非嵌套条件消耗时间", ()->{
			DLoop.loop(0, 1000000, 1, i->{
			});
		});
		
		TestTime.test("非嵌套条件消耗时间", ()->{
			DLoop.loop(0, 1000000, 1, i->{
			});
		});
	}
}
