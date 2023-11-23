package commands;

import lib.commands.Command;
import lib.main.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.Arrays;

/**
 * @author Patrick Ubelhor
 * @version 10/16/2022
 */
public class Reverse extends Command {
	
	public Reverse(Permission permission) {
		super("reverse", permission);
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		String result = this.reverseText(Arrays.copyOfRange(args, 1, args.length));
		channel.sendMessage(result).queue();
	}
	
	
	@Override
	public void runSlash(SlashCommandInteractionEvent event) {
		String input = "";
		for (OptionMapping option : event.getOptions()) {
			if (option.getName().equals("string")) {
				input = option.getAsString();
			}
		}
		
		String result = this.reverseText(input.split(" "));
		event.reply(result).queue();
	}
	
	
	private String reverseText(String[] words) {
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
	
	
	public String getShortDescription() {
		return "Reverses the character order in some text";
	}
	
	
	@Override
	public CommandData getCommandData() {
		return super.getDefaultCommandData(getShortDescription())
			.addOption(
				OptionType.STRING,
				"string",
				"The string to reverse",
				true
			);
	}
	
}
