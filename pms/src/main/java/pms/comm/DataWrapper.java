package pms.comm;

import pms.util.comm.Info;

public class DataWrapper {
	private Object data;
	private String msg;
	// 参数 0失败 1成功
	private int suc = -1;
	// 主要提示关于账号的存在信息 -1表示不存在 0 表示未被注册 1 表示已被注册
	private int ret = -1;

	public int getSuc() {
		return suc;
	}

	public DataWrapper setSuc(int suc) {
		this.suc = suc;
		return this;
	}

	public Object getData() {
		return data;
	}

	public DataWrapper setData(Object data) {
		this.data = data;
		return this;
	}

	public int getRet() {
		return ret;
	}

	public DataWrapper setRet(int ret) {
		this.ret = ret;
		return this;
	}

	public String getMsg() {
		return msg;
	}

	public DataWrapper setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public DataWrapper wrapInfo(Info info) {
		this.msg = info.getInfo();
		this.ret = this.suc = info.getStatus();
		this.data = info.getData();
		return this;
	}

	public DataWrapper suc(String info) {
		this.ret = 0;
		this.suc = 0;
		this.msg = info;
		return this;
	}

	public DataWrapper fail(String info) {
		this.ret = -1;
		this.suc = -1;
		this.msg = info;
		return this;
	}

	public DataWrapper msg(String msg, int ret) {
		this.ret = ret;
		this.suc = ret < 0 ? -1 : 0;
		this.msg = msg;
		return this;
	}
}
