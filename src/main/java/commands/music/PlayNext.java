package commands.music;

import lib.music.Music;
import lib.music.QueueNextAudioLoadResultHandler;
import lib.music.TrackScheduler;
import lib.main.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author Patrick Ubelhor, Evan Perry Grove
 * @version 5/16/2021, 5/4/2018
 *
 */
public final class PlayNext extends Music {

	public PlayNext(Permission permission) {
		super("playnext", permission);
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		
		if (args.length < 2) return;
		
		// Joins the voice channel if not in one
		if (!joinAudioChannel(event)) {
			return; // If we failed to join a voice channel, return
		}
		
		if (args[1].startsWith("http") || args[1].startsWith("www")) {
			TrackScheduler trackScheduler = Music.trackSchedulers.get(event.getGuild().getIdLong());
			playerManager.loadItem(args[1], new QueueNextAudioLoadResultHandler(trackScheduler));
		}
	}
	
	
	@Override
	public String getUsage() {
		return getName() + " <url>";
	}
	
	
	@Override
	public String getDescription() {
		return "Plays the audio from a YouTube video, placing it at the front of the playback queue instead of the rear. given as 'url'";
	}
	
}
