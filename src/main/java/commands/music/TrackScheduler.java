package commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import main.Bot;
import net.dv8tion.jda.core.entities.Game;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor, Evan Perry Grove
 * @version 2/9/2019, 5/4/2018
 */
class TrackScheduler extends AudioEventAdapter {
	
	private final AudioPlayer player;
	private final BlockingDeque<AudioTrack> queue;
	
	TrackScheduler(AudioPlayer player) {
		this.player = player;
		this.queue = new LinkedBlockingDeque<>();
	}
	
	
	/**
	 * Essentially a proxy to player#startTrack(), but updates the bot's Discord status
	 * with the name of this song.
	 *
	 *
	 * @param track The track to start playing, passing null will stop the current track and return false
	 * @param noInterrupt Whether to only start if nothing else is playing
	 * @return True if the track was started
	 */
	private boolean startTrack(AudioTrack track, boolean noInterrupt) {
		boolean result = player.startTrack(track, noInterrupt);
		
		if (track == null) { // If the playback is killed...
			Bot.getJDA().getPresence().setGame(null);
			
		} else if (result) { // Else if there is a song, and we succeeded in playing it...
			logger.info("Now playing: " + track.getInfo().title);
			Game status = Game.playing("Playing " + track.getInfo().title);
			Bot.getJDA().getPresence().setGame(status);
		}
		
		return result;
	}
	
	
	/**
	 * Adds the track to the end of the playback queue.
	 *
	 * @param track The song to enqueue.
	 */
	void queue(AudioTrack track) {
		
		if (!startTrack(track, true)) {
			logger.info("Adding to queue");
			queue.offer(track);
		}
	}
	
	
	/**
	 * Adds the track to the beginning of the playback queue.
	 *
	 * @param track The song to push onto the queue.
	 */
	void queueNext(AudioTrack track) {
		if(!startTrack(track, true)) {
			logger.info("Adding to front of queue");
			queue.offerFirst(track);
		}
	}
	
	
	/**
	 * Plays the next song in the queue, ending playback of the current active track if necessary.
	 * If there are no more tracks left in the queue, this will terminate playback.
	 */
	void playNext() {
		AudioTrack next = queue.poll();
		logger.info(next == null ? "end of queue" : next.getInfo().title);
		startTrack(next, false);
	}
	
	
	/**
	 * Clears the playback queue and ends the song currently playing.
	 */
	void clearQueue() {
		queue.clear();
		player.startTrack(null, false);
	}
	
	
	String getCurrentSong() {
		if (player.getPlayingTrack() == null) return null;
		
		return player.getPlayingTrack().getInfo().title;
	}
	
	
	List<String> getQueue() {
		var titles = new LinkedList<String>();
		queue.forEach(audioTrack -> titles.addLast(audioTrack.getInfo().title));
		
		return titles;
	}
	
	/**
	 * Pauses the current active track.
	 */
	void pause() {
		if (player.getPlayingTrack() != null) {
			player.setPaused(true);
		}
	}
	
	
	/**
	 * Continues playback of the current active track.
	 */
	void unpause() {
		player.setPaused(false);
	}
	
	
	/**
	 * @return The number of tracks remaining in the playback queue (excluding the active track).
	 */
	int getQueueLength() {
		return queue.size();
	}
	
	
	@Override
	public void onPlayerPause(AudioPlayer player) {}
	
	
	@Override
	public void onPlayerResume(AudioPlayer player) {}
	
	
	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		logger.info("Track has begun");
		player.getPlayingTrack().setPosition(0);
	}
	
	
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		logger.info("Ended");
		
		if (endReason.mayStartNext) {
			playNext();
		}
		
		// endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
		// endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
		// endReason == STOPPED: The player was stopped.
		// endReason == REPLACED: Another track started playing while this had not finished
		// endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
		//                       clone of this back to your queue
	}
	
	
	@Override
	public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
		// An already playing track threw an exception (track end event will still be received separately)
		logger.warn("Threw exception", exception);
	}
	
	
	@Override
	public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
		logger.warn("Track is stuck");
		playNext();
	}
	
}
