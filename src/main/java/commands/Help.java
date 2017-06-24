package commands;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author PatrickUbelhor
 * @version 6/24/2017
 */
public class Help extends Command {
	
	
	public void run(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		StringBuilder msg = new StringBuilder("```\n");
		
		for (Command c : Command.getCommandMap().values()) {
			msg.append("\t*").append(c.getUsage()).append("*\n");
			msg.append(c.getDescription()).append("\n\n");
		}
		msg.append("```");
		
		channel.sendMessage(msg.toString()).queue();
	}
	
	
	public String getUsage() {
		return "help";
	}
	
	
	public String getDescription() {
		return "Prints a message containing all bot commands and their descriptions";
	}
	
}
