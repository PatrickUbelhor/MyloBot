package services;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 2/19/2020
 */
class PeriodicRunner extends Thread {
	
	private final long period;
	private final Runnable action;
	
	PeriodicRunner(String name, long period, Runnable action) {
		super(name);
		this.period = period;
		this.action = action;
	}
	
	
	public void run() {
		boolean isActive = true;
		
		while (isActive) {
			logger.info(String.format("%s running...", this.getName()));
			action.run();
			
			try {
				Thread.sleep(period);
			} catch (InterruptedException e) {
				logger.debug(this.getName() + " thread interrupted");
				isActive = false;
			}
		}
		
		logger.info(this.getName() + " thread killed");
	}
	
}
