package commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor, Evan Perry Grove
 * @version 5/5/2018, 5/4/2018
 */
class TrackScheduler extends AudioEventAdapter {
	
	private final AudioPlayer player;
	private final BlockingDeque<AudioTrack> queue;
	
	TrackScheduler(AudioPlayer player) {
		this.player = player;
		this.queue = new LinkedBlockingDeque<>();
	}


	/**
	 * Adds the track to the end of the playback queue.
	 *
	 * @param track The song to enqueue.
	 */
	void queue(AudioTrack track) {
		
		if (!player.startTrack(track, true)) {
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
		if(!player.startTrack(track, true)) {
			logger.info("Adding to front of queue");
			queue.offerFirst(track);
		}
	}


	/**
	 * Plays the next song in the queue, ending playback of the current active track if necessary.
	 * If there are no more tracks left in the queue, this will terminate playback.
	 */
	void playNext() {
		logger.info("Playing next track: ");
		AudioTrack next = queue.poll();
		logger.info(next == null ? "end of queue" : next.getInfo().title);
		player.startTrack(next, false);
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
		logger.warn("Threw exception");
	}


	@Override
	public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
		logger.warn("Track is stuck");
		playNext();
	}
	
}
