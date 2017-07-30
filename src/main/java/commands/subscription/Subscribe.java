package commands.subscription;

import commands.Command;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author PatrickUbelhor
 * @version 7/16/2017
 */
public class Subscribe extends Command {
	
	
	/**
	 * Loads all the services.
	 *
	 * @return True if any single service was initialized. False if all failed.
	 */
	public boolean subInit() {
		boolean passed = false;
		
		for (Service s : Service.getServices().values().toArray(new Service[] {})) {
			if (!s.loadSubscribers() || !s.loadData()) {
				Service.getServices().remove(s.getName());
			} else {
				passed = true;
			}
		}
		return passed;
	}
	
	
	/**
	 * Safely ends all services.
	 *
	 * @return True.
	 */
	public boolean subEnd() {
		for (Service s : Service.getServices().values().toArray(new Service[] {})) {
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
		
		Service s = Service.getServices().get(args[1].toLowerCase());
		
		if (s == null) {
			channel.sendMessage("Unknown or unavailable service").queue();
			return;
		}
		
		channel.sendMessage(s.subscribe(event, args)).queue();
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
