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
		
		// Make a <usage, description> Pair for each command. We use StringBuilder to format within the pair
		LinkedList<Pair<StringBuilder, StringBuilder>> entries = new LinkedList<>();
		for (Command c : Command.getCommandMap().values()) {
			StringBuilder usage = new StringBuilder(c.getUsage());
			StringBuilder description = new StringBuilder(c.getDescription());
			Pair<StringBuilder, StringBuilder> entry = new Pair<>(usage, description);
			entries.addLast(entry);
		}

		channel.sendMessage(format(entries)).queue();
	}
	
	
	private String format(List<Pair<StringBuilder, StringBuilder>> entries) {
		int maxKeyLength = -1;
		
		// Find length of longest key
		for (Pair<StringBuilder, StringBuilder> entry : entries) {
			int keyLength = entry.getKey().length();
			if (keyLength > maxKeyLength) {
				maxKeyLength = keyLength;
			}
		}
		
		
		// Right-justify each 'usage' and wrap each 'description' TODO: remove '64' magic number
		leftJustify(entries, maxKeyLength);
		wrap(entries, maxKeyLength + 2, 64); // +2 comes from ": " after usage
		
		// Construct message string
		StringBuilder msg = new StringBuilder("```");
		for (Pair<StringBuilder, StringBuilder> entry : entries) {
			
			// "key: value\n"
			msg.append(entry.getKey());
			msg.append(": ");
			msg.append(entry.getValue());
			msg.append('\n');
		}
		msg.append("```");
		
		return msg.toString();
	}
	
	
	private void leftJustify(List<Pair<StringBuilder, StringBuilder>> entries, int length) {
		for (Pair<StringBuilder, StringBuilder> entry : entries) {
			StringBuilder key = entry.getKey();
			int keyLength = key.length(); // Original length (can't check in loop while modifying)
			
			// Pad key with spaces on left side
			for (int i = 0; i < length - keyLength; i++) {
				entry.getKey().append(' ');
			}
		}
	}
	
	
	private void wrap(List<Pair<StringBuilder, StringBuilder>> entries, int leftPadding, int length) {
		for (Pair<StringBuilder, StringBuilder> entry : entries) {
			StringBuilder val = entry.getValue();
			
			// Break overflow into new lines, then add left padding
			for (int i = length; i < val.length(); i += length + leftPadding) { // Start at 'length' because first line is padded by 'usage'
				val.insert(i, '\n');
				
				// Insert left padding
				for (int j = 1; j <= leftPadding; j++) { // Start at 1 to insert after newline
					val.insert(i + j, ' ');
				}
			}
		}
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
