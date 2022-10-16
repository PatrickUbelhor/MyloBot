package commands;

import lib.commands.Command;
import lib.main.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.Arrays;

/**
 * @author Patrick Ubelhor
 * @version 10/14/2022
 */
public class Reverse extends Command {
	
	public Reverse(Permission permission) {
		super("reverse", permission);
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		
		MessageChannel channel = event.getChannel();
		StringBuilder msg = new StringBuilder();
		
		String result = this.run(Arrays.copyOfRange(args, 1, args.length));
		// Print first token. Then add a space before every following token
		msg.append(args[1]);
		for (int i = 2; i < args.length; i++) {
			msg.append(' ');
			msg.append(args[i]);
		}
		
		channel.sendMessage(msg.reverse().toString()).queue();
	}
	
	
	private String run(String[] words) {
		StringBuilder msg = new StringBuilder();
		
		// Print first token. Then add a space before every following token
		for (String word : words) {
			msg.append(" ");
			msg.append(word);
		}
		
		return msg.reverse().toString();
	}
	
	
	@Override
	public String getUsage() {
		return getName() + " <message>";
	}
	
	
	@Override
	public String getDescription() {
		return "Reverses the character order in the message";
	}
	
	
	@Override
	public CommandData getCommandData() {
		return super.getDefaultCommandData()
			.addOption(
				OptionType.STRING,
				"string",
				"The string to reverse",
				true
			);
	}
	
}
