package commands.subscription;

import java.util.List;
import java.util.function.Supplier;
import net.dv8tion.jda.core.entities.MessageChannel;

import static main.Globals.logger;

/**
 * @author PatrickUbelhor
 * @version 8/16/2017
 */
final class CheckerThread extends Thread {
	
	private final long delayTime;
	private final Supplier<List<String>> checkFunction;
	private final MessageChannel mediaChannel;
	
	CheckerThread(String name, long delayTime, Supplier<List<String>> checkFunction, MessageChannel mediaChannel) {
		super(name);
		this.delayTime = delayTime;
		this.checkFunction = checkFunction;
		this.mediaChannel = mediaChannel;
	}
	
	
	public void run() {
		boolean isActive = true;
		
		while (isActive) {
			
			logger.info(String.format("%s running...", this.getName()));
			
			List<String> responses = checkFunction.get();
			for (String s : responses) {
				mediaChannel.sendMessage(s).queue(); // Send message to channel
			}
			
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
