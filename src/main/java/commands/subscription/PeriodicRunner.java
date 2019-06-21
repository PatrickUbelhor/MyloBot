package commands.subscription;

import java.util.List;
import java.util.function.Supplier;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 8/16/2017
 */
final class PeriodicRunner extends Thread {
	
	private final long delayTime;
	private final Supplier<List<MessageContent>> checkFunction;
	private final MessageChannel mediaChannel;
	
	PeriodicRunner(String name, long delayTime, Supplier<List<MessageContent>> checkFunction, MessageChannel mediaChannel) {
		super(name);
		this.delayTime = delayTime;
		this.checkFunction = checkFunction;
		this.mediaChannel = mediaChannel;
	}
	
	
	public void run() {
		boolean isActive = true;
		
		while (isActive) {
			
			logger.info(String.format("%s running...", this.getName()));
			
			List<MessageContent> responses = checkFunction.get();
			for (MessageContent mc : responses) {
				MessageBuilder messageBuilder = new MessageBuilder();
				messageBuilder.append(mc.getLink());
				messageBuilder.append("\n");
				
				for (User u : mc.getSubscribers()) {
					messageBuilder.append(u);
				}
				
				mediaChannel.sendMessage(messageBuilder.build()).queue(); // Send message to channel
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
