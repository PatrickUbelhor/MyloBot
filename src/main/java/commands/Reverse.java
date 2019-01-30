package commands;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author Patrick Ubelhor
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
		
		// Print first token. Then add a space before every following token
		msg.append(args[1]);
		for (int i = 2; i < args.length; i++) {
			msg.append(' ');
			msg.append(args[i]);
		}
		
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
