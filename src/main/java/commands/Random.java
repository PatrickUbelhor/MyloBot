package commands;

import lib.commands.Command;
import lib.main.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Patrick Ubelhor
 * @version 10/16/2022
 */
public class Random extends Command {
	
	private static final Logger logger = LogManager.getLogger(Random.class);
	
	public Random(Permission permission) {
		super("random", permission);
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		if (args.length < 2) {
			event.getChannel().sendMessage("Usage: " + getUsage()).queue();
			return;
		}
		
		TextChannel channel = event.getTextChannel();
		long min = 1;
		long max;
		
		// Parse upper bound
		String maxString = (args.length == 2) ? args[1] : args[2]; // arg number depends on whether a min was specified
		try {
			max = Long.parseLong(maxString);
		} catch (NumberFormatException e) {
			logger.debug("User entered invalid upper bound: '{}'", maxString);
			channel.sendMessage("I think you specified an invalid upper bound: '" + maxString + "'").queue();
			return;
		}
		
		// Only parse lower bound if there are enough arguments
		if (args.length > 2) {
			// Parse the lower bound
			try {
				min = Long.parseLong(args[1]);
			} catch (NumberFormatException e) {
				logger.debug("User entered invalid number: '{}'", args[1]);
				channel.sendMessage("I think you specified an invalid number: '" + args[1] + "'").queue();
				return;
			}
		}
		
//		// Make sure 'max' is positive
//		if (max <= 0) {
//			logger.debug("User entered non-positive upper bound: '{}'", max);
//			channel.sendMessage("The upper bound must be positive!").queue();
//			return;
//		}
		
//		// Make sure 'min' is non-negative
//		if (min < 0) {
//			logger.debug("User entered negative lower bound: '{}'", min);
//			channel.sendMessage("The lower bound can't be negative!").queue();
//			return;
//		}
		
		long rand = generateRandomNumber(min, max);
		channel.sendMessage(rand + "").queue();
	}
	
	@Override
	public void runSlash(SlashCommandEvent event) {
		long min = 0;
		long max = 1;
		
		for (OptionMapping option : event.getOptions()) {
			switch (option.getName()) {
				case "min" -> min = option.getAsLong();
				case "max" -> max = option.getAsLong();
			}
		}
		
		long rand = generateRandomNumber(min, max);
		event.getChannel().sendMessage(rand + "").queue();
	}
	
	private long generateRandomNumber(long min, long max) {
		long range = max - min;
		long rand = new java.util.Random().nextLong(range) + min;
		logger.debug("Generated: {}", rand);
		
		return rand;
	}
	
	@Override
	public String getUsage() {
		return "random [min] <max>";
	}
	
	@Override
	public String getDescription() {
		return "Generates a random number in the given range";
	}
	
	@Override
	public CommandData getCommandData() {
		return super.getDefaultCommandData()
			.addOptions(
				new OptionData(OptionType.INTEGER, "max", "The max number in the range (exclusive)", true),
				new OptionData(OptionType.INTEGER, "min", "The min number in the range (inclusive)", false)
			);
	}
	
}
