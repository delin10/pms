package pms.util.schedule.impl;

import pms.util.schedule.Controllable;

public abstract class ControllableRunnable implements Controllable,Runnable {
	protected boolean stop=false;
	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop(){
		this.stop=true;
	}

}
