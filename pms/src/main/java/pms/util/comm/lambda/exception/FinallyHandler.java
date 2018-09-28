package pms.util.comm.lambda.exception;

import pms.util.comm.lambda.exception.SimpleExec.DataWrapper;

public interface FinallyHandler{
	public void handle(DataWrapper data);
}
