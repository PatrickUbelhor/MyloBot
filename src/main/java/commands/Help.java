package commands;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author PatrickUbelhor
 * @version 6/21/2017
 */
public class Help extends Command {
	
	
	public void run(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		String msg = "```\n";
		
		for (Command c : Command.getCommandMap().values()) {
			msg += c.getUsage() + "\n\t" + c.getDescription() + "\n";
		}
		msg += "```";
		
		channel.sendMessage(msg).queue();
	}
	
	
	public String getUsage() {
		return "help";
	}
	
	
	public String getDescription() {
		return "Prints a message containing all bot commands and their\n\tdescriptions";
	}
	
}
