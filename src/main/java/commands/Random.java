package commands;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 05/20/2018
 */
public class Random extends Command {
	
	public Random() {
		super("random");
	}
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		if (args.length < 2) {
			event.getChannel().sendMessage("Usage: " + getUsage()).queue();
			return;
		}
		
		TextChannel channel = event.getTextChannel();
		int min = 0;
		int max;
		
		// Parse upper bound
		String maxString = (args.length == 2) ? args[1] : args[2]; // arg number depends on whether a min was specified
		try {
			max = Integer.parseInt(maxString);
		} catch (NumberFormatException e) {
			logger.debug("User entered invalid upper bound: '" + maxString + "'");
			channel.sendMessage("I think you specified an invalid upper bound: '" + maxString + "'").queue();
			return;
		}
		
		// Only parse lower bound if there are enough arguments
		if (args.length > 2) {
			// Parse the lower bound
			try {
				min = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				logger.debug("User entered invalid number: '" + args[1] + "'");
				channel.sendMessage("I think you specified an invalid number: '" + args[1] + "'").queue();
				return;
			}
		}
		
		// Make sure 'max' is positive
		if (max <= 0) {
			logger.debug("User entered non-positive upper bound: '" + max + "'");
			channel.sendMessage("The upper bound must be positive!").queue();
			return;
		}
		
		// Make sure 'min' is non-negative
		if (min < 0) {
			logger.debug("User entered negative lower bound: '" + min + "'");
			channel.sendMessage("The lower bound can't be negative!").queue();
			return;
		}
		
		
		int rand = new java.util.Random().nextInt(max - min) + min; // Generate random number in range, plus minimum value
		logger.info("Generated: " + rand);
		channel.sendMessage(rand + "").queue();
	}
	
	
	@Override
	public String getUsage() {
		return "random [min] <max>";
	}
	
	
	@Override
	public String getDescription() {
		return "Generates a random number between 'min' (inclusive) and 'max' (exclusive). " +
				"If no min is specified, it defaults to 0";
	}
	
}
