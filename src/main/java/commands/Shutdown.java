package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author PatrickUbelhor
 * @version 06/10/2017
 */
public class Shutdown extends Command {
	
	public void run(MessageReceivedEvent event, String[] args) {
		for (Command command : getCommandList()) {
			command.end();
			System.out.printf("Killed %s\n", command.getName());
		}
		System.out.println("Commands all killed");
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
