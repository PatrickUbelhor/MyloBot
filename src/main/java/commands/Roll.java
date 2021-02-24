package commands;

import lib.commands.Command;
import lib.main.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 2/23/2021
 */
public class Roll extends Command {
	
	public Roll(Permission permission) {
		super("roll", permission);
	}
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		if (args.length < 2) {
			event.getChannel().sendMessage("Usage: " + getUsage()).queue();
			return;
		}
		
		TextChannel channel = event.getTextChannel();
		
		// Parse input
		String[] splitDieArg = args[1].split("d");
		int numDice = Integer.parseInt(splitDieArg[0]);
		int maxVal = Integer.parseInt(splitDieArg[1]);
		
		if (maxVal == 0) {
			channel.sendMessage("0").queue();
			return;
		}
		
		// Roll the dice and sum them
		int roll = 0;
		Random random = new Random();
		for (int i = 0; i < numDice; i++) {
			roll += random.nextInt(maxVal) + 1;
		}
		
		logger.info("Rolled: {}", roll);
		channel.sendMessage(roll + "").queue();
	}
	
	
	@Override
	public String getUsage() {
		return "roll <num_dice>d<dice_value>\nExample: 'roll 4d6' rolls 4x 6-sided dice";
	}
	
	
	@Override
	public String getDescription() {
		return "Rolls a number of dice";
	}
	
}
