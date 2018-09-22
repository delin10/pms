package pms.util.system.hook;

public class HookUtil {
	public static void addHook(Hook hook) {
		Runtime.getRuntime().addShutdownHook(new Thread(()->{
			hook.hook();
		}));
	}
}
