package pms.comm;

import pms.util.comm.Info;

public class DataWrapper {
	private Object data;
	private String msg;
	//���� 0ʧ�� 1�ɹ�
	private int suc = -1;
	//��Ҫ��ʾ�����˺ŵĴ�����Ϣ -1��ʾ������ 0 ��ʾδ��ע�� 1 ��ʾ�ѱ�ע��
	private int ret = -1;
	public int getSuc() {
		return suc;
	}
	public void setSuc(int suc) {
		this.suc = suc;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public int getRet() {
		return ret;
	}
	public void setRet(int ret) {
		this.ret = ret;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public void wrapInfo(Info info) {
		this.msg=info.getInfo();
		this.ret=this.suc=info.getStatus();
		this.data=info;
	}
	
	public void suc(String info) {
		this.ret=0;
		this.suc=0;
		this.msg=info;
	}
	
	public void fail(String info) {
		this.ret=-1;
		this.suc=-1;
		this.msg=info;
	}
}
