package commands;

import javafx.util.Pair;
import main.Bot;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Patrick Ubelhor
 * @version 6/12/2018
 */
public class Help extends Command {
	
	private static final int SIZE_OF_TAB = 4;
	private static final int DESCRIPTION_WRAP_LENGTH = 64;
	private static final int DESCRIPTION_WRAP_INDENT = 2;
	private static final int MAX_MSG_LENGTH = 2000; // Defined by Discord TODO: see if there's a predefined constant for this
	private static final String SEPARATOR = " : ";
	
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

		// Send the messages to the channel
		List<String> messages = format(entries);
		for (String msg : messages) {
			// TODO: Check if there is a race condition. If so, use callback with List iterator to send next messages
			channel.sendMessage(msg).queue();
		}
	}
	
	
	private List<String> format(List<Pair<StringBuilder, StringBuilder>> entries) {
		int maxKeyLength = -1;
		
		// Find length of longest key
		for (Pair<StringBuilder, StringBuilder> entry : entries) {
			int keyLength = entry.getKey().length();
			if (keyLength > maxKeyLength) {
				maxKeyLength = keyLength;
			}
		}
		
		// Left-justify each 'usage' and wrap each 'description'
		leftJustify(entries, maxKeyLength);
		wrap(entries, maxKeyLength + SEPARATOR.length(), DESCRIPTION_WRAP_LENGTH);
		
		return compile(entries);
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
				int lastSpace = val.lastIndexOf(" ", i);
				val.setCharAt(lastSpace, '\n');
				
				// Insert left padding (tabs)
				for (int j = 1; j <= (leftPadding + DESCRIPTION_WRAP_INDENT) / SIZE_OF_TAB; j++) {
					val.insert(lastSpace + j, '\t');
				}
				
				// Insert left padding (spaces)
				for (int j = 1; j <= (leftPadding + DESCRIPTION_WRAP_INDENT) % SIZE_OF_TAB; j++) {
					val.insert(lastSpace + j, ' ');
				}
			}
		}
	}
	
	
	private List<String> compile(List<Pair<StringBuilder, StringBuilder>> entries) {
		LinkedList<String> messages = new LinkedList<>();
		
		StringBuilder msg = new StringBuilder("```");
		for (Pair<StringBuilder, StringBuilder> entry : entries) {
			int entryLength = entry.getKey().length() + SEPARATOR.length() + entry.getValue().length();
			
			if (msg.length() + entryLength + 3 > MAX_MSG_LENGTH) { // +3 for "```" terminator
				msg.append("```");
				messages.addLast(msg.toString());
				msg = new StringBuilder();
			}
			
			msg.append(entry.getKey());
			msg.append(SEPARATOR);
			msg.append(entry.getValue());
			msg.append('\n');
		}
		msg.append("```");
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
	
}
