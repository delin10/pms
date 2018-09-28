package pms.util.comm.lambda.exception;

public class SimpleExec {
	public static Object exec(Procedure procedure,Handler handler) {
		DataWrapper wrapper=new DataWrapper();
		try {
			return procedure.process(wrapper);
		}catch(Exception e) {
			handler.handle(e);
			return null;
		}
		
	}
	
	public static Object exec(Procedure procedure,Handler handler,FinallyHandler handler_finally) {
		DataWrapper wrapper=new DataWrapper();
		try {
			return procedure.process(wrapper);
		}catch(Exception e) {
			handler.handle(e);
			return null;
		}finally {
			handler_finally.handle(wrapper);
		}
		
	}
	
	public static class DataWrapper{
		private Object value;

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}
	}
}
