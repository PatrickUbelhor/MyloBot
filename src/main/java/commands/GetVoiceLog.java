package commands;

import log.VoiceTracker;
import main.Globals;
import main.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.io.IOException;

/**
 * @author Patrick Ubelhor
 * @version 11/2/2019
 */
public class GetVoiceLog extends Command {
	
	private final VoiceTracker tracker;
	
	public GetVoiceLog(Permission perm, VoiceTracker tracker) {
		super("getvclog", perm);
		this.tracker = tracker;
	}
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		TextChannel channel = event.getTextChannel();
		File logFile = new File("vclog.csv");
		
		channel.sendFile(logFile).queue();
	}
	
	
	@Override
	public boolean subEnd() {
		try {
			tracker.close();
		} catch (IOException e) {
			Globals.logger.error(e);
			return false;
		}
		
		return true;
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
