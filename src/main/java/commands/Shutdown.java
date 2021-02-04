package commands;

import lib.commands.Command;
import main.Bot;
import lib.main.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 2/27/2020
 */
public class Shutdown extends Command {

	public Shutdown(Permission perm) {
		super("shutdown", perm);
	}

	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		
		logger.info("Shutting down...");
		
		logger.info("Killing commands...");
		Bot.getCommands().values()
				.parallelStream()
				.forEach(command -> {
					command.end();
					logger.info("\tKilled {}", command.getName());
				});
		logger.info("All commands killed");
		
		logger.info("Killing services...");
		Bot.getServices().values()
				.parallelStream()
				.forEach(service -> {
					service.endThread();
					logger.info("\tKilled {}", service.getName());
				});
		logger.info("All services killed");
		
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
