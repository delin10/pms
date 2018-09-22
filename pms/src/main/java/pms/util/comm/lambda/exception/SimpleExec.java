package pms.util.comm.lambda.exception;

public class SimpleExec {
	public static Object exec(Procedure procedure,Handler handler) {
		try {
			return procedure.process();
		}catch(Exception e) {
			handler.handle(e);
			return null;
		}
		
	}
}
