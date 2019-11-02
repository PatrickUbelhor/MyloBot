package commands;

import main.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;

/**
 * @author Patrick Ubelhor
 * @version 11/2/2019
 */
public class GetVoiceLog extends Command {
	
	public GetVoiceLog(Permission perm) {
		super("getvclog", perm);
	}
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		TextChannel channel = event.getTextChannel();
		File logFile = new File("vclog.csv");
		
		channel.sendFile(logFile).queue();
	}
	
	
	@Override
	public String getUsage() {
		return "getvclog";
	}
	
	
	@Override
	public String getDescription() {
		return "Get log of people joining/leaving voice channels";
	}
	
}
