package commands;

import lib.commands.Command;
import log.VoiceTrackerFileWriter;
import lib.main.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * @author Patrick Ubelhor
 * @version 11/2/2019
 */
public class GetVoiceLog extends Command {
	
	private static final Logger logger = LogManager.getLogger(GetVoiceLog.class);
	
	private final VoiceTrackerFileWriter tracker;
	
	public GetVoiceLog(Permission perm, VoiceTrackerFileWriter tracker) {
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
			logger.error(e);
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
