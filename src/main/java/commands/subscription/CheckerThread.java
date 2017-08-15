package commands.subscription;

import static main.Globals.logger;

/**
 * @author PatrickUbelhor
 * @version 8/15/2017
 */
abstract class CheckerThread extends Thread {
	
	private final long delayTime;
	
	protected CheckerThread(String name, long delayTime) {
		super(name);
		this.delayTime = delayTime;
	}
	
	
	protected abstract void check();
	
	
	public void run() {
		boolean isActive = true;
		
		while (isActive) {
			
			logger.info(String.format("%s running...", this.getName()));
			check();
			logger.info(String.format("%s finished", this.getName()));
			
			try {
				Thread.sleep(delayTime);
			} catch (InterruptedException e) {
				logger.debug(String.format("%s update thread interrupted", this.getName()));
				isActive = false;
			}
		}
		
		logger.info(String.format("%s update thread killed", this.getName()));
	}
	
}
