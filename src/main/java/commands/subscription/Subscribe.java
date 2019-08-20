package commands.subscription;

import commands.Command;
import main.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author Patrick Ubelhor
 * @version 1/29/2019
 */
public class Subscribe extends Command {
	
	public Subscribe() {
		super("sub");
	}
	
	public Subscribe(Permission perm) {
		super("sub", perm);
	}
	
	
	/**
	 * Loads all the services.
	 *
	 * @return True if any single service was initialized. False if all failed.
	 */
	@Override
	public boolean subInit() {
		return Service.initAll();
	}
	
	
	/**
	 * Safely ends all services.
	 *
	 * @return True.
	 */
	@Override
	public boolean subEnd() {
		Service.endAll();
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
		return "Subscribes a channel to a checker service, like Twitch or S@20";
	}
	
}
