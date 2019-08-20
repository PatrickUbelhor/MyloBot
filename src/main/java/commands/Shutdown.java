package commands;

import main.Bot;
import main.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 8/26/2018
 */
public class Shutdown extends Command {

	public Shutdown(Permission perm) {
		super("shutdown", perm);
	}

	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		
		logger.info("Shutting down...");
		Bot.getCommands().values()
				.parallelStream()
				.forEach(command -> {
					command.end();
					logger.info(String.format("\tKilled %s", command.getName()));
				});
		
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
