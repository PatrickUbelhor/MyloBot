package commands.subscription;

import commands.Command;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author PatrickUbelhor
 * @version 7/16/2017
 */
public class Subscribe extends Command {
	
	private static final CheckTwitch twitch = new CheckTwitch();
	private static final CheckSurrender surrender = new CheckSurrender();
	
	
	/**
	 * Loads all the services.
	 *
	 * @return True if any single service was initialized. False if all failed.
	 */
	public boolean subInit() {
		
		for (Service s : Service.getServiceMap().values().toArray(new Service[] {})) {
			s.init();
		}
		
		return true;
	}
	
	
	/**
	 * Safely ends all services.
	 *
	 * @return True.
	 */
	public boolean subEnd() {
		for (Service s : Service.getServiceMap().values().toArray(new Service[] {})) {
			s.end();
		}
		return true;
	}
	
	
	@Override
	public final void run(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		
		if (args.length < 2) {
			channel.sendMessage("Too few args").queue();
			return;
		}
		
		Service s = Service.getServiceMap().get(args[1].toLowerCase());
		
		if (s == null) {
			channel.sendMessage("Unknown or unavailable service").queue();
			return;
		}
		
		s.subscribe((args.length > 2) ? args[2] : null, event.getAuthor());
	}
	
	@Override
	public final String getUsage() {
		return "sub <serviceName>";
	}
	
	@Override
	public String getDescription() {
		return "Subscribes a channel to a checker service, like Twitch or S@20.";
	}
	
}
