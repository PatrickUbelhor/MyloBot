package commands;

import javafx.util.Pair;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Patrick Ubelhor
 * @version 6/10/2018
 */
public class Help extends Command {
	
	public Help() {
		super("help");
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
//		StringBuilder msg = new StringBuilder();
//
//		for (Command c : Command.getCommandMap().values()) {
//
//			msg.append(String.format(
//					"``%s``\n\t%s\n\n",
//					c.getUsage(),
//					c.getDescription()
//			));
//		}
		
		LinkedList<Pair<String, String>> entries = new LinkedList<>();
		for (Command c : Command.getCommandMap().values()) {
			Pair<String, String> entry = new Pair<>(c.getUsage(), c.getDescription());
			entries.addLast(entry);
		}

//		channel.sendMessage(msg.toString()).queue();
		channel.sendMessage(format(entries)).queue();
	}
	
	
	private String format(List<Pair<String, String>> entries) {
		int maxKeyLength = -1;
		
		// Find length of longest key
		for (Pair<String, String> entry : entries) {
			int keyLength = entry.getKey().length();
			if (keyLength > maxKeyLength) {
				maxKeyLength = keyLength;
			}
		}
		
		// Prepend enough spaces to right-justify the keys (assume monospace)
		StringBuilder sb = new StringBuilder("```");
		
		for (Pair<String, String> entry : entries) {
			int keyLength = entry.getKey().length();
			
			// Pad with spaces to right-justify
			for (int i = 0; i < maxKeyLength - keyLength; i++) {
				sb.append(' ');
			}
			
			// "key: value\n"
			sb.append(entry.getKey());
			sb.append(": ");
			sb.append(entry.getValue());
			sb.append('\n');
		}
		sb.append("```");
		
		return sb.toString();
	}
	
	
	@Override
	public String getUsage() {
		return getName();
	}
	
	
	@Override
	public String getDescription() {
		return "Prints a message containing all bot commands and their descriptions";
	}
	
}
