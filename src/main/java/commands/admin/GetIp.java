package commands.admin;

import clients.ShellClient;
import lib.commands.Command;
import lib.main.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * @author Patrick Ubelhor
 * @version 10/21/2022
 */
public class GetIp extends Command {

	private final ShellClient shellClient = new ShellClient();

	public GetIp(Permission perm) {
		super("getIP", perm);
	}

	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		String ip = shellClient.getIp();

		if (ip == null) {
			channel.sendMessage("An error occurred when trying to get my IP").queue();
			return;
		}

		channel.sendMessage(ip).queue();
	}

	@Override
	public void runSlash(SlashCommandInteractionEvent event) {
		String ip = shellClient.getIp();

		if (ip == null) {
			event.reply("An error occurred when trying to get my IP").queue();
			return;
		}

		event.reply(ip).queue();
	}

	@Override
	public String getUsage() {
		return getName();
	}

	@Override
	public String getDescription() {
		return "Gets the IP of the server";
	}

	@Override
	public SlashCommandData getCommandData() {
		return super.getDefaultCommandData();
	}

}
