package pms.util.comm.lambda.exception;

public interface Handler {
	public static CarelessException CARELESS=new CarelessException();
	public static PrintTrace PRINTTRACE=new PrintTrace();
	public void handle(Exception e);
}
