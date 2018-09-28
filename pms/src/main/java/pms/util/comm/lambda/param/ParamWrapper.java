package pms.util.comm.lambda.param;

public class ParamWrapper {
	private Object data;
	private ParamWrapper() {}
	
	public static ParamWrapper instance() {
		return new ParamWrapper();
	}
	
	public ParamWrapper set(Object o) {
		data=o;
		return this;
	}
	
	public Object get() {
		return data;
	}
}
