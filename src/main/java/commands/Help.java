package commands;

import lib.commands.Command;
import lib.main.Permission;
import main.Bot;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Patrick Ubelhor
 * @version 11/27/2023
 */
public class Help extends Command {
	
	private static final char BACKTICK = '`';
	
	public Help(Permission permission) {
		super("help", permission);
	}


	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		
		List<String> entries = getCommandEntries();

		// Send the messages to the channel
		List<String> messages = generateMessages(entries);
		for (String msg : messages) {
			channel.sendMessage(msg).queue();
		}
	}


	@Override
	public void runSlash(SlashCommandInteractionEvent event) {
		List<String> entries = getCommandEntries();
		List<String> messages = generateMessages(entries);
		for (String msg : messages) {
			event.reply(msg).queue();
		}
	}

	
	private List<String> getCommandEntries() {
		LinkedList<String> entries = new LinkedList<>();
		for (Command command : Bot.getCommands().values()) {
			StringBuilder entry = new StringBuilder()
					.append(BACKTICK)
					.append(command.getUsage())
					.append(BACKTICK)
					.append("\n")
					.append(command.getDescription());
			
			entries.addLast(entry.toString());
		}
		
		return entries;
	}

	
	private List<String> generateMessages(List<String> entries) {
		LinkedList<String> messages = new LinkedList<>();
		
		StringBuilder msg = new StringBuilder();
		for (String entry : entries) {
			if (msg.length() + entry.length() + 2 > Message.MAX_CONTENT_LENGTH) { // +2 for extra "\n\n"
				messages.addLast(msg.toString());
				msg = new StringBuilder();
			}
			
			msg.append(entry);
			msg.append("\n\n");
		}
		messages.addLast(msg.toString());
		
		return messages;
	}

	
	@Override
	public String getUsage() {
		return getName();
	}

	
	@Override
	public String getDescription() {
		return "Prints a message containing all bot commands and their descriptions";
	}


	@Override
	public SlashCommandData getCommandData() {
		return super.getDefaultCommandData();
	}
	
}
