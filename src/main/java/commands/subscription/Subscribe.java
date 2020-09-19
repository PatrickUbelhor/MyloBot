package commands.subscription;

import commands.Command;
import main.Bot;
import main.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import services.MessageSubscriber;
import services.Service;
import services.Subscriber;

/**
 * @author Patrick Ubelhor
 * @version 9/19/2020
 */
public class Subscribe extends Command {
	
	public Subscribe() {
		super("sub");
	}
	
	public Subscribe(Permission perm) {
		super("sub", perm);
	}
	
	
	@Override
	public final void run(MessageReceivedEvent event, String[] args) {
		TextChannel channel = event.getTextChannel();
		
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
		MessageSubscriber.getInstance().addSubscriber(service.getName(), sub);
	}
	
	
	@Override
	public final String getUsage() {
		return getName() + " <serviceName>";
	}
	
	
	@Override
	public String getDescription() {
		return "Subscribes a channel to a checker service, like IPChange or S@20";
	}
	
}
