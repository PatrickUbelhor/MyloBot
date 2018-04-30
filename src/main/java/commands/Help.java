package commands;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author Patrick Ubelhor
 * @version 4/30/2018
 */
public class Help extends Command {
	
	public Help() {
		super("help");
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		StringBuilder msg = new StringBuilder();
		
		for (Command c : Command.getCommandMap().values()) {

			msg.append(String.format(
					"``%s``\n\t%s\n\n",
					c.getUsage(),
					c.getDescription()
			));
		}

		channel.sendMessage(msg.toString()).queue();
	}
	
	
	@Override
	public String getUsage() {
		return getName();
	}
	
	
	@Override
	public String getDescription() {
		return "Prints a message containing all bot commands and their descriptions";
	}
	
}
