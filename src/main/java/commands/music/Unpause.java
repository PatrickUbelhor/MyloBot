package commands.music;

import lib.commands.Command;
import lib.music.MusicManager;
import lib.music.TrackScheduler;
import lib.main.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * @author Patrick Ubelhor
 * @version 12/24/2025
 */
public class Unpause extends Command {
	
	public Unpause(Permission permission) {
		super("unpause", permission);
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		TrackScheduler trackScheduler = MusicManager.getInstance().getTrackScheduler(event.getGuild().getIdLong());
		trackScheduler.unpause();
	}
	
	@Override
	public void runSlash(SlashCommandInteractionEvent event) {
		TrackScheduler trackScheduler = MusicManager.getInstance().getTrackScheduler(event.getGuild().getIdLong());
		trackScheduler.unpause();
	}
	
	@Override
	public String getUsage() {
		return getName();
	}
	
	@Override
	public String getDescription() {
		return "Continues playing a paused track";
	}
	
	public String getShortDescription() {
		return getDescription();
	}
	
	@Override
	public SlashCommandData getCommandData() {
		return super.getDefaultCommandData();
	}
	
}
