package pms.util.schedule;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import pms.util.schedule.impl.ControllableRunnable;

public class Scheduler {
	private static int CORE_SIZE = 10;
	private static ScheduledExecutorService exec = null;
	private static Map<String, Controllable> runs;

	public Scheduler init(int coresize, boolean daemon) {
		if (exec == null) {
			CORE_SIZE = coresize;
			exec = Executors.newScheduledThreadPool(coresize, new ThreadFactory() {
				public Thread newThread(Runnable r) {
					Thread t = Executors.defaultThreadFactory().newThread(r);
					t.setDaemon(daemon);
					return t;
				}
			});
		}

		if (runs == null) {
			runs = new HashMap<>();
		}
		return this;
	}

	public void schedule(Runnable run, int delay, TimeUnit timeunit) {
		exec.scheduleAtFixedRate(run, delay, delay, timeunit);
	}

	public Controllable scheduleControllable(Runnable run, int delay, TimeUnit timeunit) {
		ControllableRunnable thread = new ControllableRunnable() {
			@Override
			public void run() {
				if (this.stop) {
					Thread.currentThread().interrupt();
				}
				run.run();
			}
		};
		exec.scheduleAtFixedRate(thread, delay, delay, timeunit);
		return thread;
	}
	
	public Controllable scheduleControllable(Runnable run, int delay, TimeUnit timeunit,String id) {
		Controllable run_ = scheduleControllable(run,delay,timeunit);
		runs.put(id, run_);
		return run_;
	}

	public void shutdown(String id) {
		Controllable run = runs.get(id);
		if (run != null) {
			run.stop();
		}
	}

	public void shutdownNow() {
		exec.shutdownNow();
	}

	public static int getCORE_SIZE() {
		return CORE_SIZE;
	}
}
