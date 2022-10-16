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

import java.util.Random;

/**
 * @author Patrick Ubelhor
 * @version 10/15/2022
 */
public class Roll extends Command {
	
	private static final Logger logger = LogManager.getLogger(Roll.class);
	
	public Roll(Permission permission) {
		super("roll", permission);
	}
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		if (args.length < 2) {
			event.getChannel().sendMessage("Usage: " + getUsage()).queue();
			return;
		}
		
		// Parse input
		String[] splitDieArg = args[1].split("d");
		long numDice = Long.parseLong(splitDieArg[0]);
		long maxVal = Long.parseLong(splitDieArg[1]);
		
		long result = run(numDice, maxVal);
		TextChannel channel = event.getTextChannel();
		channel.sendMessage(result + "").queue();
	}
	
	@Override
	public void runSlash(SlashCommandEvent event) {
		long numDice = 0;
		long sides = 0;
		
		for (OptionMapping option : event.getOptions()) {
			switch (option.getName()) {
				case "numDice" -> numDice = option.getAsLong();
				case "sides" -> sides = option.getAsLong();
			}
		}
		
		long result = run(numDice, sides);
		event.getChannel()
			.sendMessage(result + "")
			.queue();
	}
	
	private long run(long numDice, long sides) {
		if (numDice < 1 || sides == 0) {
			return 0;
		}
		
		// Roll the dice and sum them
		int roll = 0;
		Random random = new Random();
		for (int i = 0; i < numDice; i++) {
			roll += random.nextLong(sides) + 1;
		}
		
		return roll;
	}
	
	@Override
	public String getUsage() {
		return "roll <num_dice>d<dice_value>\nExample: 'roll 4d6' rolls 4x 6-sided dice";
	}
	
	@Override
	public String getDescription() {
		return "Rolls a number of dice";
	}
	
	@Override
	public CommandData getCommandData() {
		return super.getDefaultCommandData()
			.addOptions(
				new OptionData(OptionType.INTEGER, "numDice", "Number of dice to roll", true),
				new OptionData(OptionType.INTEGER, "sides", "Type of die to roll; ie. 6 for a d6 or 20 for a d20", true)
			);
	}
	
}
