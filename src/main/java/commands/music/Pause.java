package commands.music;

import lib.commands.music.Music;
import lib.commands.music.TrackScheduler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author Patrick Ubelhor
 * @version 2/24/2021
 */
public class Pause extends Music {
	
	public Pause() {
		super("pause");
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
