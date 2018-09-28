package pms.util.system.hook;

import java.util.ArrayList;

public class HookUtil {
	private static ArrayList<Hook> hooks = new ArrayList<>();

	public static void addHook(Hook hook) {
		hooks.add(hook);
		if (hooks.size() == 1) {
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				hooks.forEach(hook_ -> hook_.hook());
			}));
		}
	}
}
