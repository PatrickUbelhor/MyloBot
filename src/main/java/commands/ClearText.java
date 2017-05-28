package commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author PatrickUbelhor
 * @version 2/16/2017
 */
public class ClearText extends Command {
	
	
	public void run(MessageReceivedEvent event, String[] args) {
		TextChannel ch = event.getTextChannel();
		
		if (args.length < 2) {
			event.getChannel().sendMessage("Usage: " + getUsage()).queue();
			return;
		}
		
		int num = Integer.parseInt(args[1]);
		
		try {
			CompletableFuture<List<Message>> task = new CompletableFuture<>();
			ch.getHistory().retrievePast(num).queue(task::complete, task::completeExceptionally);
			List<Message> list = task.get();
			
			for (Message m : list) {
				ch.deleteMessageById(m.getId()).queue();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public String getUsage() {
		return "clear <num>";
	}
	
	public String getDescription() {
		return "Deletes 'num' amount of messages from the chat";
	}
	
}
