package commands.admin;

import commands.Command;
import main.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 8/20/2019
 */
public class ClearText extends Command {
	
	private static final int MAX_MESSAGE_COUNT = 100; // JDA throws exception after this point
	
	public ClearText(Permission perm) {
		super("clear", perm);
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		TextChannel channel = event.getTextChannel();
		
		if (args.length < 2) {
			event.getChannel().sendMessage("Usage: " + getUsage()).queue();
			return;
		}
		
		int num;
		try {
			num = Integer.parseInt(args[1]) + 1; // Plus one to delete the command itself too
		} catch (NumberFormatException e) {
			channel.sendMessage("I think you specified an invalid number of messages to delete: '" + args[1] + "'").queue();
			return;
		}
		
		logger.info("Retrieving and deleting message history...");
		MessageHistory messageHistory = new MessageHistory(channel);
		
		// Retrieve the list of messages to delete
		for (int i = 0; i < num / MAX_MESSAGE_COUNT; i++) {
			messageHistory.retrievePast(MAX_MESSAGE_COUNT).complete();
		}
		messageHistory.retrievePast(num % MAX_MESSAGE_COUNT).complete();
		
		deleteAll(channel, messageHistory.getRetrievedHistory());
		logger.info("Message history deleted.");
	}
	
	
	// TODO: Use java library to properly calculate elapsed time
	/**
	 * Checks if a message is at least two weeks old.
	 *
	 * @param m The message to check.
	 * @return True if the message at least two weeks old.
	 */
	private boolean isTwoWeeksOld(Message m) {
		OffsetDateTime now = OffsetDateTime.now();
		OffsetDateTime then = m.getTimeCreated();
		
		// We know there won't be any Discord messages from pre-2014
		// Convert the instant into the number of days since 1/1/2014
		int today = (now.getYear() - 2014) * 365 + now.getDayOfYear();
		int creationDay = (then.getYear() - 2014) * 365 + then.getDayOfYear();
		
		return (today - creationDay) > 12; // Day of padding just in case
	}
	
	
	@Override
	public String getUsage() {
		return getName() + " <num>";
	}
	
	
	@Override
	public String getDescription() {
		return "Deletes 'num' amount of messages from the chat";
	}
	
	
	private void deleteGroup(TextChannel channel, List<Message> messages) {
		for (int i = 0; i < messages.size() / MAX_MESSAGE_COUNT; i++) {
			List<Message> del = messages.subList(i * MAX_MESSAGE_COUNT, (i + 1) * MAX_MESSAGE_COUNT);
			channel.deleteMessages(del).queue();
		}
		
		// Get remaining sublist
		List<Message> del = messages.subList(messages.size() - (messages.size() % MAX_MESSAGE_COUNT), messages.size());
		
		switch (del.size()) {
			case 0:
				break;
			case 1:
				del.get(0).delete().queue();
				break;
			default:
				channel.deleteMessages(del).queue();
		}
	}
	
	
	private void deleteAll(TextChannel channel, List<Message> messages) {
		if (messages.size() == 0) return;
		logger.debug("Size of message list: " + messages.size());
		
		// Singly delete all old messages
		messages.parallelStream()
				.filter(this::isTwoWeeksOld)
				.forEach(message -> message.delete().queue());
		
		// Bulk delete all young messages
		List<Message> bulkMessages = messages.parallelStream()
				.filter(msg -> !isTwoWeeksOld(msg))
				.collect(Collectors.toList());
		
		deleteGroup(channel, bulkMessages);
	}
	
}
