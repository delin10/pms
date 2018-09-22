package pms.util.servlet.asyn;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;

import pms.util.servlet.asyn.intf.Task;

public class DAsyn {
	private AsyncContext asyncContext;
	
	public DAsyn createAsynContext(HttpServletRequest request) {
		asyncContext=request.startAsync();
		return this;
	}
	
	public void handle(Task task) {
		asyncContext.start(() -> {
			task.handle();
			asyncContext.complete();
		});
	}
}
