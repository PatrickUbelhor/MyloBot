package commands.subscription;

import commands.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author PatrickUbelhor
 * @version 06/25/2017
 */
public class Unsubscribe extends Command {
	
	
	@Override
	public final void run(MessageReceivedEvent event, String[] args) {
		if (args.length < 2) {
			System.out.println("Too few args");
			return;
		}
		
		Service s = Service.getServiceMap().get(args[1]);
		
		if (s == null) {
			event.getChannel().sendMessage("Unknown or unavailable service");
			return;
		}
		
		s.unsubscribe((args.length > 2) ? args[2] : null, event.getAuthor());
	}
	
	@Override
	public final String getUsage() {
		return "unsub <serviceName>";
	}
	
	@Override
	public String getDescription() {
		return "Unsubscribes a channel from a checker service, like Twitch or S@20.";
	}
	
}
