package commands;

import lib.commands.Command;
import lib.main.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
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
 * @version 11/22/2023
 */
public class Roll extends Command {
	
	private static final Logger logger = LogManager.getLogger(Roll.class);
	private static final String OPTION_NUM_DICE = "num_dice";
	private static final String OPTION_NUM_FACES = "num_faces";
	
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
		
		long result = roll(numDice, maxVal);
		MessageChannel channel = event.getChannel();
		channel.sendMessage(result + "").queue();
	}
	
	@Override
	public void runSlash(SlashCommandInteractionEvent event) {
		long numDice = 0;
		long sides = 0;
		
		for (OptionMapping option : event.getOptions()) {
			switch (option.getName()) {
				case OPTION_NUM_DICE -> numDice = option.getAsLong();
				case OPTION_NUM_FACES -> sides = option.getAsLong();
			}
		}
		
		long result = roll(numDice, sides);
		String response = "Roll %dd%d:\n%d".formatted(numDice, sides, result);
		event.reply(response).queue();
	}
	
	private long roll(long numDice, long sides) {
		if (numDice < 1 || sides == 0) {
			return 0;
		}
		
		// Roll the dice and sum them
		long roll = 0;
		Random random = new Random();
		for (long i = 0; i < numDice; i++) {
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
	
	public String getShortDescription() {
		return getDescription();
	}
	
	@Override
	public CommandData getCommandData() {
		return super.getDefaultCommandData()
			.addOptions(
				new OptionData(OptionType.INTEGER, OPTION_NUM_DICE, "Number of dice to roll", true),
				new OptionData(OptionType.INTEGER, OPTION_NUM_FACES, "Number of faces on the die", true)
			);
	}
	
}
