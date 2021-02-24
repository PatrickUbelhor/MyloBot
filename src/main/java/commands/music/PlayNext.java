package commands.music;

import lib.commands.music.Music;
import lib.commands.music.QueueNextAudioLoadResultHandler;
import lib.commands.music.TrackScheduler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author Patrick Ubelhor, Evan Perry Grove
 * @version 2/24/2021, 5/4/2018
 *
 */
public final class PlayNext extends Music {

	private boolean active;

	public PlayNext() {
		super("playnext");
		active = false;
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		
		if (args.length < 2) return;
		
		// Joins the voice channel if not in one
		if (!active) {
			if (!joinAudioChannel(event)) {
				return; // If we failed to join a voice channel, return
			}
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
