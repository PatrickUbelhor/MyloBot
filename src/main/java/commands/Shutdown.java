package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import static main.Globals.logger;

/**
 * @author PatrickUbelhor
 * @version 8/15/2017
 */
public class Shutdown extends Command {
	
	public Shutdown() {
		super("shutdown");
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		
		logger.info("Shutting down...");
		for (Command command : getCommandMap().values()) {
			command.end();
			logger.info(String.format("\tKilled %s", command.getName()));
		}
		logger.info("All commands killed");
		event.getJDA().shutdown();
		logger.info("Shutdown complete");
	}
	
	
	@Override
	public String getUsage() {
		return getName();
	}
	
	
	@Override
	public String getDescription() {
		return "Safely shuts down the bot";
	}
	
}
