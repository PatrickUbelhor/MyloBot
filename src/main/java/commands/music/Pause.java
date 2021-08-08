package commands.music;

import lib.music.Music;
import lib.music.TrackScheduler;
import lib.main.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author Patrick Ubelhor
 * @version 5/16/2021
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
	public String getUsage() {
		return getName();
	}
	
	@Override
	public String getDescription() {
		return "Pauses the current track. Can be continued with '!unpause'";
	}
	
}
