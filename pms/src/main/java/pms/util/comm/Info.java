package pms.util.comm;

/**
 * 作为传递消息得工具
 * @author delin
 *status 表示操作是否成功,通常-1表示失败
 *info 描述操作的结果信息
 *data 携带数据
 */
public class Info {
	private int status=-1;
	private String info;
	private Object data;
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public void fail(String info) {
		this.info=info;
		this.status=-1;
	}
	
	public void suc(String info) {
		this.info=info;
		this.status=0;
	}
	
	public void wrapInfo(Info info) {
		this.status=info.getStatus();
		this.info=info.getInfo();
		this.data=info.getData();
	}
	
	public Info line() {
		this.info+="</br>";
		return this;
	}
	
	public Info append(String info) {
		this.info+=info;
		return this;
	}
}
