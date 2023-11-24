package commands.subscription;

import lib.commands.Command;
import main.Bot;
import lib.main.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import lib.services.MessageSubscriber;
import lib.services.Service;
import lib.services.Subscriber;

/**
 * @author Patrick Ubelhor
 * @version 5/16/2021
 */
public class Unsubscribe extends Command {
	
	public Unsubscribe(Permission permission) {
		super("unsub", permission);
	}
	
	
	@Override
	public final void run(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		
		if (args.length < 2) {
			channel.sendMessage("Too few args").queue();
			return;
		}
		
		Service service = Bot.getServices().get(args[1].toLowerCase());
		
		if (service == null) {
			channel.sendMessage("Unknown or unavailable service").queue();
			return;
		}
		
		Subscriber sub = new Subscriber(channel.getIdLong());
		MessageSubscriber.getInstance().removeSubscriber(service.getName(), sub);
	}
	
	@Override
	public final String getUsage() {
		return getName() + " <serviceName>";
	}
	
	
	@Override
	public String getDescription() {
		return "Unsubscribes a channel from a checker service, like IPChange or S@20";
	}
	
}
