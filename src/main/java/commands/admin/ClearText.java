package commands.admin;

import commands.Command;
import main.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.time.OffsetDateTime;
import java.util.List;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 5/10/2018
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

		delete(channel, messageHistory.getRetrievedHistory());
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
		OffsetDateTime then = m.getCreationTime();

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


	private void deleteSingly(TextChannel channel, List<Message> messages) {
		if (messages.size() == 0) return;

		for (Message msg : messages) {
			channel.deleteMessageById(msg.getId()).queue();
		}
	}


	private void deleteGroup(TextChannel channel, List<Message> messages) {
		for (int i = 0; i < messages.size() / MAX_MESSAGE_COUNT; i++) {
			List<Message> del = messages.subList(i * MAX_MESSAGE_COUNT, (i + 1) * MAX_MESSAGE_COUNT);
			channel.deleteMessages(del).queue();
		}

		// Get remaining sublist
		List<Message> del = messages.subList(messages.size() - (messages.size() % MAX_MESSAGE_COUNT), messages.size());

		// Make sure the sublist is large enough to group delete. Else, singly delete.
		if (del.size() > 2) {
			channel.deleteMessages(del).queue();
		} else {
			deleteSingly(channel, del);
		}
	}


	private void delete(TextChannel channel, List<Message> messages) {
		if (messages.size() == 0) return;

		logger.debug("Size of message list: " + messages.size());

		// If all the messages are old, we have to singly delete the whole list
		// This is just an optimization. Not logically necessary
		if (isTwoWeeksOld(messages.get(0))) {
			deleteSingly(channel, messages);
			return;
		}

		// Find index of last 'young' message
		int split = -1;
		for (int i = messages.size() - 1; i > 0; i--) {
			if (!isTwoWeeksOld(messages.get(i))) {
				split = i;
				break;
			}
		}

		// Finally delete messages
		deleteGroup(channel, messages.subList(0, split + 1)); // Group delete 'young' messages
		deleteSingly(channel, messages.subList(split + 1, messages.size())); // Singly delete 'old' messages
	}

}
