package commands;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author PatrickUbelhor
 * @version 2/16/2017
 */
public class Reverse extends Command {
	
	public void run(MessageReceivedEvent event, String[] args) {
		
		MessageChannel channel = event.getChannel();
		String msg = "";
		
		for (int i = 1; i < args.length; i++) {
			msg += args[i];
		}
		
		String result = "";
		for (char c : msg.toCharArray()) {
			result = c + result;
		}
		
		channel.sendMessage(result).queue();
	}
	
	
	public String getUsage() {
		return "reverse <message>";
	}
	
	
	public String getDescription() {
		return "Reverses the character order in the message";
	}
	
}
