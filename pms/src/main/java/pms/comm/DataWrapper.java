package pms.comm;

import pms.util.comm.Info;

public class DataWrapper {
	private Object data;
	private String msg;
	//参数 0失败 1成功
	private int suc = -1;
	//主要提示关于账号的存在信息 -1表示不存在 0 表示未被注册 1 表示已被注册
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
