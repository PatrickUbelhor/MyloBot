package lib.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Patrick Ubelhor
 * @version 8/11/2021
 */
public class QueueNextAudioLoadResultHandler implements AudioLoadResultHandler {
	
	private static final Logger logger = LogManager.getLogger(QueueNextAudioLoadResultHandler.class);
	
	private final TrackScheduler trackScheduler;
	
	public QueueNextAudioLoadResultHandler(TrackScheduler trackScheduler) {
		this.trackScheduler = trackScheduler;
	}
	
	@Override
	public void trackLoaded(AudioTrack track) {
		logger.info("[Music] Loaded track to front of playback queue");
		trackScheduler.queueNext(track);
	}
	
	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		logger.info("[Music] Loaded playlist to front of playback queue");
		for (AudioTrack track : playlist.getTracks()) {
			trackScheduler.queueNext(track);
		}
	}
	
	@Override
	public void noMatches() {
		// Notify the user that we've got nothing
		logger.info("[Music] No matches!");
	}
	
	@Override
	public void loadFailed(FriendlyException exception) {
		// Notify the user that everything exploded
		logger.error("[Music] Load failed!", exception);
		logger.error("[Music] Severity: {}", exception.severity.name());
	}
}
