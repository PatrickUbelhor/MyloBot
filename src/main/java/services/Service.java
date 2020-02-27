package services;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 2/27/2020
 */
public abstract class Service {
	
	private final String name;
	private final long period;
	private PeriodicRunner thread = null;
	
	protected Service(String name, long period) {
		this.name = name.toLowerCase();
		this.period = period;
	}
	
	
	public final String getName() {
		return name;
	}
	
	
	protected abstract boolean init();
	protected abstract void execute();
	protected abstract boolean end();
	public abstract String getInfo();
	
	
	private boolean initProxy() {
		if (init()) {
			logger.info("Initialized service: {}", name);
			return true;
		}
		
		logger.error("Failed to initialize service: {}", name);
		return false;
	}
	
	
	private void endProxy() {
		if (end()) {
			logger.info("Safely ended service: {}", name);
			return;
		}
		
		logger.warn("Failed to safely end service: {}", name);
	}
	
	
	public final void startThread() {
		if (thread != null && thread.isAlive()) {
			logger.warn("Tried to start PeriodicRunner '{}' when one was already active!", name);
			return;
		}
		
		thread = new PeriodicRunner(name + "Service", period, this::initProxy, this::execute, this::endProxy);
		thread.start();
	}
	
	
	public final void endThread() {
		if (thread == null) {
			logger.warn("Cannot kill null PeriodicRunner: {}", name);
			return;
		}
		
		if (!thread.isAlive()) {
			logger.warn("Cannot kill dead PeriodRunner: {}", name);
			return;
		}
		
		thread.interrupt();
		
		// Wait for thread to die before returning
		try {
			thread.join();
			logger.debug("Thread joined: " + thread.getName());
		} catch (InterruptedException e) {
			logger.error("Interrupted while killing service: {}", name, e);
		}
	}
	
}
