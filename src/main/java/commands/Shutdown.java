package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author PatrickUbelhor
 * @version 6/21/2017
 */
public class Shutdown extends Command {
	
	public void run(MessageReceivedEvent event, String[] args) {
		
		System.out.println("Shutting down...");
		for (Command command : getCommandMap().values()) {
			command.end();
			System.out.printf("\tKilled %s\n", command.getName());
		}
		System.out.println("All commands killed");
		event.getJDA().shutdown();
		System.out.println("Shutdown complete");
	}
	
	
	public String getUsage() {
		return "shutdown";
	}
	
	
	public String getDescription() {
		return "Safely shuts down the bot";
	}
	
}
