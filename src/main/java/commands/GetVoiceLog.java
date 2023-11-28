package commands;

import lib.commands.Command;
import lib.main.Permission;
import log.VoiceTrackerFileWriter;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * @author Patrick Ubelhor
 * @version 11/28/2023
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
		MessageChannel channel = event.getChannel();
		File logFile = new File("vclog.csv");
		FileUpload upload = FileUpload.fromData(logFile);
		channel.sendFiles(upload).queue();
	}

	@Override
	public void runSlash(SlashCommandInteractionEvent event) {
		File logFile = new File("vclog.csv");
		FileUpload upload = FileUpload.fromData(logFile);
		event.replyFiles(upload).queue();
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

	@Override
	public SlashCommandData getCommandData() {
		return super.getDefaultCommandData()
			.setDefaultPermissions(DefaultMemberPermissions.DISABLED);
	}

}
