package commands.music;

import lib.commands.Command;
import lib.main.Permission;
import lib.music.MusicManager;
import lib.music.TrackScheduler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * @author Patrick Ubelhor
 * @version 12/24/2025
 */
public class Disconnect extends Command {

	public Disconnect(Permission permission) {
		super("disconnect", permission);
	}

	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		MusicManager musicManager = MusicManager.getInstance();
		if (musicManager.leaveAudioChannel(event)) {
			TrackScheduler trackScheduler = musicManager.getTrackScheduler(event.getGuild().getIdLong());
			trackScheduler.clearQueue();
		}
	}

	@Override
	public void runSlash(SlashCommandInteractionEvent event) {
		event.reply("Leaving call").queue();
		MusicManager musicManager = MusicManager.getInstance();
		if (musicManager.leaveAudioChannel(event)) {
			TrackScheduler trackScheduler = musicManager.getTrackScheduler(event.getGuild().getIdLong());
			trackScheduler.clearQueue();
		}
	}

	@Override
	public String getUsage() {
		return getName();
	}

	@Override
	public String getDescription() {
		return "Disconnects from the voice chat";
	}

	@Override
	public SlashCommandData getCommandData() {
		return super.getDefaultCommandData();
	}

}
