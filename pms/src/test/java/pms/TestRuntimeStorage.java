package pms;

import pms.comm.RuntimeStorage;
import pms.util.test.DLoop;
import pms.util.test.TestTime;

public class TestRuntimeStorage {
	public static void main(String[] args) {
		//�ȹ�������ͳ�ʼ�����ʱ���ʱ��
		TestTime.test("Ƕ����������ʱ��", ()->{
			DLoop.loop(0, 100, 1, i->{
				RuntimeStorage.getBuildingService();
			});
		});
		
		TestTime.test("Ƕ����������ʱ��", ()->{
			DLoop.loop(0, 1000000, 1, i->{
				RuntimeStorage.getBuildingService();
			});
		});
		
		TestTime.test("��Ƕ����������ʱ��", ()->{
			DLoop.loop(0, 1000000, 1, i->{
				RuntimeStorage.getBuildingService();
			});
		});
		
		TestTime.test("��Ƕ����������ʱ��", ()->{
			DLoop.loop(0, 1000000, 1, i->{
			});
		});
		
		TestTime.test("��Ƕ����������ʱ��", ()->{
			DLoop.loop(0, 1000000, 1, i->{
			});
		});
		
		TestTime.test("��Ƕ����������ʱ��", ()->{
			DLoop.loop(0, 1000000, 1, i->{
			});
		});
	}
}
