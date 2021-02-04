package lib.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 8/15/2017
 */
public class QueueLastAudioLoadResultHandler implements AudioLoadResultHandler {
	private final TrackScheduler trackScheduler;
	
	public QueueLastAudioLoadResultHandler(TrackScheduler trackScheduler) {
		this.trackScheduler = trackScheduler;
	}
	
	@Override
	public void trackLoaded(AudioTrack track) {
		logger.info("Loaded track");
		trackScheduler.queue(track);
	}
	
	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		logger.info("Loaded playlist");
		for (AudioTrack track : playlist.getTracks()) {
			trackScheduler.queue(track);
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
