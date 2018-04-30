package commands;

import main.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 4/30/2018
 */
public class Shutdown extends Command {

	public Shutdown(Permission perm) {
		super("shutdown", perm);
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
