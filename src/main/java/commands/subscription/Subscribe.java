package commands.subscription;

import commands.Command;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author Patrick Ubelhor
 * @version 8/16/2017
 */
public class Subscribe extends Command {
	
	public Subscribe() {
		super("sub");
	}
	
	
	/**
	 * Loads all the services.
	 *
	 * @return True if any single service was initialized. False if all failed.
	 */
	@Override
	public boolean subInit() {
		return Service.init();
	}
	
	
	/**
	 * Safely ends all services.
	 *
	 * @return True.
	 */
	@Override
	public boolean subEnd() {
		Service.end();
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
		
		s.subscribe(event, (args.length > 2) ? args[2] : null);
	}
	
	
	@Override
	public final String getUsage() {
		return getName() + " <serviceName>";
	}
	
	
	@Override
	public String getDescription() {
		return "Subscribes a channel to a checker service, like Twitch or S@20.";
	}
	
}
