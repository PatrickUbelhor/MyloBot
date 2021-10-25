package lib.music.v2;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

/**
 * @author Patrick Ubelhor
 * @version 8/10/2021
 */
public class TrackScheduler {
	
	private static final Logger logger = LogManager.getLogger(TrackScheduler.class);
	
	private final AudioPlayer player;
	private final BlockingDeque<AudioTrack> queue;
	
	TrackScheduler(AudioPlayer player) {
		this.player = player;
		this.queue = new LinkedBlockingDeque<>();
	}
	
	
	public void listen(PlayerEventHandler handler) {
		handler.subscribe(this::playNext);
	}
	
	
	/**
	 * Essentially a proxy to player#startTrack(), but updates the bot's Discord status
	 * with the name of this song.
	 *
	 * @param track The track to start playing, passing null will stop the current track and return false
	 * @param noInterrupt Whether to only start if nothing else is playing
	 * @return True if the track was started
	 */
	private boolean startTrack(AudioTrack track, boolean noInterrupt) {
		boolean isValid = player.startTrack(track, noInterrupt);
		
		// If this is a valid song, and we succeeded in playing it...
		if (isValid) {
			logger.info("Now playing: " + track.getInfo().title);
		}
		
		return isValid;
	}
	
	
	/**
	 * Adds the track to the end of the playback queue.
	 *
	 * @param track The song to enqueue.
	 */
	public void queue(AudioTrack track) {
		boolean trackStarted = startTrack(track, true);
		
		if (!trackStarted) {
			logger.info("Adding to queue");
			queue.offer(track);
		}
	}
	
	
	/**
	 * Adds the track to the beginning of the playback queue.
	 *
	 * @param track The song to push onto the queue.
	 */
	public void queueNext(AudioTrack track) {
		boolean trackStarted = startTrack(track, true);
		
		if (!trackStarted) {
			logger.info("Adding to front of queue");
			queue.offerFirst(track);
		}
	}
	
	
	/**
	 * Plays the next song in the queue, ending playback of the current active track if necessary.
	 * If there are no more tracks left in the queue, this will terminate playback.
	 */
	public void playNext() {
		AudioTrack next = queue.poll();
		logger.info(next == null ? "End of queue" : next.getInfo().title);
		startTrack(next, false);
	}
	
	
	/**
	 * Removes a number of songs from the queue. This includes the song that is currently playing.
	 * If 'count - 1' is greater than the number of songs in the queue, this will just clear the queue
	 * with no errors.
	 *
	 * @param count The number of songs to skip.
	 */
	public void skip(int count) {
		logger.info("Skipping " + count + " songs");
		
		count--; // This is the number of songs from the queue to remove. We also skip currently playing song later, which will meet 'count'.
		
		if (count >= queue.size()) {
			queue.clear();
			this.playNext();
			return;
		}
		
		for (int i = 0; i < count; i++) {
			queue.removeFirst();
		}
		
		this.playNext();
	}
	
	
	/**
	 * Clears the playback queue and ends the song currently playing.
	 */
	public void clearQueue() {
		queue.clear();
		player.startTrack(null, false);
	}
	
	
	/**
	 * @return The title of the currently playing song.
	 */
	public String getCurrentSong() {
		if (player.getPlayingTrack() == null) return null;
		
		return player.getPlayingTrack().getInfo().title;
	}
	
	
	/**
	 * @return List of track titles in order within queue.
	 */
	public List<String> getQueue() {
		return queue.stream()
				.map(audioTrack -> audioTrack.getInfo().title)
				.collect(Collectors.toList());
	}
	
	
	/**
	 * Pauses the current active track.
	 */
	public void pause() {
		if (player.getPlayingTrack() != null) {
			player.setPaused(true);
		}
	}
	
	
	/**
	 * Continues playback of the current active track.
	 */
	public void unpause() {
		player.setPaused(false);
	}
	
}
