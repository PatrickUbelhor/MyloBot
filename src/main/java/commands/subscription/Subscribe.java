package commands.subscription;

import commands.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author PatrickUbelhor
 * @version 6/25/2017
 */
public class Subscribe extends Command {
	
	
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
	
	
	public boolean subEnd() {
		for (Service s : Service.getServices().values().toArray(new Service[] {})) {
			s.end();
		}
		return true;
	}
	
	
	@Override
	public final void run(MessageReceivedEvent event, String[] args) {
		if (args.length < 2) {
			System.out.println("Too few args");
			return;
		}
		
		Service s = Service.getServices().get(args[1].toLowerCase());
		
		if (s == null) {
			event.getChannel().sendMessage("Unknown or unavailable service");
			return;
		}
		
		event.getChannel().sendMessage(s.subscribe(event, args)).queue();
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
