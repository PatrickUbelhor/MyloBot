package commands.music;

import lib.music.Music;
import lib.music.TrackScheduler;
import lib.main.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/**
 * @author Patrick Ubelhor
 * @version 10/16/2022
 */
public class Pause extends Music {
	
	public Pause(Permission permission) {
		super("pause", permission);
	}
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		TrackScheduler trackScheduler = Music.trackSchedulers.get(event.getGuild().getIdLong());
		trackScheduler.pause();
	}
	
	@Override
	public void runSlash(SlashCommandEvent event) {
		TrackScheduler trackScheduler = Music.trackSchedulers.get(event.getGuild().getIdLong());
		trackScheduler.pause();
	}
	
	@Override
	public String getUsage() {
		return getName();
	}
	
	@Override
	public String getDescription() {
		return "Pauses the current track. Can be continued with '!unpause'.";
	}
	
	public String getShortDescription() {
		return "Pauses the current track";
	}
	
	@Override
	public CommandData getCommandData() {
		return super.getDefaultCommandData();
	}
	
}
