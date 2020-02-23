package services;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 2/19/2020
 */
public abstract class Service {
	
	private final String name;
	private final long period;
	private final Runnable action;
	private PeriodicRunner thread = null;
	
	Service(String name, long period, Runnable action) {
		this.name = name;
		this.period = period;
		this.action = action;
	}
	
	
	public final String getName() {
		return name;
	}
	
	
	public final void startThread() {
		if (thread != null && thread.isAlive()) {
			logger.warn("Tried to start PeriodicRunner '" + name + "' when one was already active!");
			return;
		}
		
		thread = new PeriodicRunner(name + "Service", period, action);
		thread.start();
	}
	
	
	public final void endThread() {
		if (thread == null) {
			logger.warn("Cannot kill null PeriodicRunner: " + name);
			return;
		}
		
		if (!thread.isAlive()) {
			logger.warn("Cannot kill dead PeriodRunner: " + name);
			return;
		}
		
		thread.interrupt();
		
		// Wait for thread to die before returning
		try {
			thread.join();
			logger.debug("Thread joined: " + thread.getName());
		} catch (InterruptedException e) {
			logger.error("Interrupted while killing service: " + name, e);
		}
	}
	
}
