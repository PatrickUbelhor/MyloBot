package commands.admin;

import lib.commands.Command;
import lib.main.Permission;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 10/11/2019
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
		
		channel.purgeMessages(messageHistory.getRetrievedHistory());
		logger.info("Message history deleted.");
	}
	
	
	@Override
	public String getUsage() {
		return getName() + " <num>";
	}
	
	
	@Override
	public String getDescription() {
		return "Deletes 'num' amount of messages from the chat";
	}
	
}
