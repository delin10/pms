package pms.util.comm.lambda.exception;

public class PrintTrace implements Handler{

	@Override
	public void handle(Exception e) {
		// TODO Auto-generated method stub
		e.printStackTrace();
	}

}
