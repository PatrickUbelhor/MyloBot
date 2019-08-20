package commands.subscription;

import commands.Command;
import main.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 1/29/2019
 */
public class Unsubscribe extends Command {
	
	public Unsubscribe() {
		super("unsub");
	}
	
	public Unsubscribe(Permission perm) {
		super("unsub", perm);
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
		return "Unsubscribes a channel from a checker service, like Twitch or S@20";
	}
	
}
