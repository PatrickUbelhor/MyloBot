package lib.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 5/11/2018
 */
public class QueueNextAudioLoadResultHandler implements AudioLoadResultHandler {
	private final TrackScheduler trackScheduler;
	
	public QueueNextAudioLoadResultHandler(TrackScheduler trackScheduler) {
		this.trackScheduler = trackScheduler;
	}
	
	@Override
	public void trackLoaded(AudioTrack track) {
		logger.info("Loaded track to front of playback queue");
		trackScheduler.queueNext(track);
	}
	
	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		logger.info("Loaded playlist to front of playback queue");
		for (AudioTrack track : playlist.getTracks()) {
			trackScheduler.queueNext(track);
		}
	}
	
	@Override
	public void noMatches() {
		// Notify the user that we've got nothing
		logger.info("No matches!");
	}
	
	@Override
	public void loadFailed(FriendlyException throwable) {
		// Notify the user that everything exploded
		logger.error("Load failed!");
		logger.error(throwable.severity.name());
		logger.error(throwable.getMessage());
	}
}
