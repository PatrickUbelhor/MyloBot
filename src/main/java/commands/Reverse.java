package commands;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author PatrickUbelhor
 * @version 8/15/2017
 */
public class Reverse extends Command {
	
	public Reverse() {
		super("reverse");
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		
		MessageChannel channel = event.getChannel();
		StringBuilder msg = new StringBuilder();
		
		for (int i = 1; i < args.length; i++) {
			msg.append(args[i]);
		}
		
//		StringBuilder result = new StringBuilder();
//		for (char c : msg.toString().toCharArray()) {
//			result.insert(0, c);
//		}
		
//		channel.sendMessage(result.toString()).queue();
		channel.sendMessage(msg.reverse().toString()).queue();
	}
	
	
	@Override
	public String getUsage() {
		return getName() + " <message>";
	}
	
	
	@Override
	public String getDescription() {
		return "Reverses the character order in the message";
	}
	
}
