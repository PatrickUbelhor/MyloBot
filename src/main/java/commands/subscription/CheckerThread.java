package commands.subscription;

/**
 * @author PatrickUbelhor
 * @version 7/16/2017
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
			
			System.out.printf("%s running...\n", this.getName());
			check();
			System.out.printf("%s finished\n", this.getName());
			
			try {
				Thread.sleep(delayTime);
			} catch (InterruptedException e) {
				System.out.printf("%s update thread killed", this.getName());
				isActive = false;
			}
		}
		
	}
	
}
