package commands;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 05/16/2018
 */
public class Random extends Command {
	
	public Random() {
		super("random");
	}
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		TextChannel channel = event.getTextChannel();
		
		if (args.length < 2) {
			event.getChannel().sendMessage("Usage: " + getUsage()).queue();
			return;
		}
		
		int num;
		try {
			num = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			logger.debug("User entered non-number: '" + args[1] + "'");
			channel.sendMessage("I think you specified an invalid number: '" + args[1] + "'").queue();
			return;
		}
		
		// Make sure it's not a negative number
		if (num <= 0) {
			logger.debug("User entered negative number: '" + num + "'");
			channel.sendMessage("``random`` cannot take negative numbers").queue();
			return;
		}
		
		
		int rand = new java.util.Random().nextInt(num);
		channel.sendMessage(rand + "").queue();
	}
	
	
	@Override
	public String getUsage() {
		return "random <num>";
	}
	
	
	@Override
	public String getDescription() {
		return "Generates a random number between 0 (inclusive) and <num> (exclusive)";
	}
	
}
