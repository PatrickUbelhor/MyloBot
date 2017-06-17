package commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author PatrickUbelhor
 * @version 6/11/2017
 */
public class ClearText extends Command {
	
	private static final int MAX_MESSAGE_COUNT = 100; // JDA throws exception after this point
	
	
	public void run(MessageReceivedEvent event, String[] args) {
		TextChannel channel = event.getTextChannel();
		
		if (args.length < 2) {
			event.getChannel().sendMessage("Usage: " + getUsage()).queue();
			return;
		}
		
		
		try {
			int num = Integer.parseInt(args[1]);
			MessageHistory messageHistory = new MessageHistory(channel);
			
			System.out.println("Retrieving and deleting message history...");
			while (num > 0) {
				
				messageHistory.retrievePast(num % MAX_MESSAGE_COUNT).complete();
				List<Message> messages = messageHistory.getRetrievedHistory();
				
				int i;
				for (i = messages.size() - 1; i > 0; i--) {
					
					if (!isTwoWeeksOld(messages.get(i))) {
						channel.deleteMessages(messages.subList(0, i + 1)).queue();
						
						for (int j = i + 1; j < messages.size(); j++) {
							channel.deleteMessageById(messages.get(j).getId()).queue();
						}
						
						break;
					}
				}
				
				if (i == 0) {
					for (Message m : messages) {
						channel.deleteMessageById(m.getId()).queue();
					}
				}
				
				num -= MAX_MESSAGE_COUNT; // It's okay if we deleted less than this, it'll still finish properly
			}
			System.out.println("Message history deleted.");
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
	}
	
	
	private boolean isTwoWeeksOld(Message m) {
		OffsetDateTime now = OffsetDateTime.now();
		OffsetDateTime then = m.getCreationTime();
		
		int today = (now.getYear() - 2014) * now.getDayOfYear();
		int creationDay = (then.getYear() - 2014) * then.getDayOfYear();
		
		return (today - creationDay) > 12; // Day of padding just in case
	}
	
	
	public String getUsage() {
		return "clear <num>";
	}
	
	public String getDescription() {
		return "Deletes 'num' amount of messages from the chat";
	}
	
}
