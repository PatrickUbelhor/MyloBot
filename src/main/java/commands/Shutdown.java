package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author PatrickUbelhor
 * @version 6/3/2017
 */
public class Shutdown extends Command {
	
	public void run(MessageReceivedEvent event, String[] args) {
		for (Command command : getCommandList()) {
			command.end();
		}
		event.getJDA().shutdown();
	}
	
	
	public String getUsage() {
		return "shutdown";
	}
	
	
	public String getDescription() {
		return "Safely shuts down the bot";
	}
	
}
