package lib.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BooleanSupplier;

/**
 * @author Patrick Ubelhor
 * @version 2/26/2020
 */
class PeriodicRunner extends Thread {
	
	private static final Logger logger = LogManager.getLogger(PeriodicRunner.class);
	
	private final long period;
	private final BooleanSupplier init;
	private final Runnable execute;
	private final Runnable end;
	
	PeriodicRunner(String name, long period, BooleanSupplier init, Runnable execute, Runnable end) {
		super(name);
		this.period = period;
		this.init = init;
		this.execute = execute;
		this.end = end;
	}
	

	@Override
	public void run() {
		// Exit the thread immediately if initialization failed
		if (!init.getAsBoolean()) {
			return;
		}
		
		boolean isActive = true;
		while (isActive) {
			logger.info("{} running...", this.getName());
			execute.run();
			
			try {
				Thread.sleep(period);
			} catch (InterruptedException e) {
				logger.debug("{} thread interrupted", this.getName());
				isActive = false;
			}
		}
		
		end.run();
		logger.info("{} thread killed", this.getName());
	}
	
}
