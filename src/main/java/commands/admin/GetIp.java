package commands.admin;

import clients.ShellClient;
import commands.Command;
import main.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author Patrick Ubelhor
 * @version 2/19/2020
 */
public class GetIp extends Command {
	
	private ShellClient shellClient = new ShellClient();
	
	public GetIp(Permission perm) {
		super("getIP", perm);
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		TextChannel channel = event.getTextChannel();
		String ip = shellClient.getIp();
		
		if (ip == null) {
			channel.sendMessage("An error occurred when trying to get my IP").queue();
			return;
		}
		
		channel.sendMessage(ip).queue();
	}
	
	
	@Override
	public String getUsage() {
		return getName();
	}
	
	
	@Override
	public String getDescription() {
		return "Gets the IP of the server";
	}
	
}
