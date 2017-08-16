package commands.subscription;

import commands.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import static main.Globals.logger;

/**
 * @author PatrickUbelhor
 * @version 8/15/2017
 */
public class Unsubscribe extends Command {
	
	public Unsubscribe() {
		super("unsub");
	}
	
	
	@Override
	public final void run(MessageReceivedEvent event, String[] args) {
		if (args.length < 2) {
			logger.warn("Too few args");
			return;
		}
		
		Service s = Service.getServiceMap().get(args[1]);
		
		if (s == null) {
			event.getChannel().sendMessage("Unknown or unavailable service").queue();
			return;
		}
		
		s.unsubscribe(event, (args.length > 2) ? args[2] : null);
	}
	
	@Override
	public final String getUsage() {
		return getName() + " <serviceName>";
	}
	
	
	@Override
	public String getDescription() {
		return "Unsubscribes a channel from a checker service, like Twitch or S@20.";
	}
	
}
