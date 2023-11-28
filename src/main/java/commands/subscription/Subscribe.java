package commands.subscription;

import lib.commands.Command;
import lib.main.Permission;
import lib.services.MessageSubscriber;
import lib.services.Service;
import lib.services.Subscriber;
import main.Bot;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * @author Patrick Ubelhor
 * @version 11/28/2023
 */
public class Subscribe extends Command {

	public Subscribe(Permission permission) {
		super("sub", permission);
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
		MessageSubscriber.getInstance().addSubscriber(service.getName(), sub);
	}

	@Override
	public void runSlash(SlashCommandInteractionEvent event) {
		String serviceName = event.getOption("service_name").getAsString().toLowerCase();
		Service service = Bot.getServices().get(serviceName);

		if (service == null) {
			event.reply("Unknown or unavailable service").queue();
			return;
		}

		MessageChannel channel = event.getChannel();
		Subscriber sub = new Subscriber(channel.getIdLong());
		MessageSubscriber.getInstance().addSubscriber(service.getName(), sub);
		event.reply("Subscribed this channel to %s".formatted(serviceName)).queue();
	}

	@Override
	public final String getUsage() {
		return getName() + " <serviceName>";
	}

	@Override
	public String getDescription() {
		return "Subscribes a channel to a checker service, like IPChange or S@20";
	}

	@Override
	public SlashCommandData getCommandData() {
		return super.getDefaultCommandData()
			.addOption(OptionType.STRING, "service_name", "The name of the periodic service", true);
	}

}
