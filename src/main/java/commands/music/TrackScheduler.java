package commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 8/15/2017
 */
class TrackScheduler extends AudioEventAdapter {
	
	private final AudioPlayer player;
	private final BlockingQueue<AudioTrack> queue;
	
	TrackScheduler(AudioPlayer player) {
		this.player = player;
		this.queue = new LinkedBlockingQueue<>();
	}
	
	void queue(AudioTrack track) {
		if (!player.startTrack(track, true)) {
			logger.info("Adding to queue");
			queue.offer(track);
		}
	}
	
	void playNext() {
		logger.info("Playing next track: ");
		AudioTrack next = queue.poll();
		logger.info(next == null ? "end of queue" : next.getInfo().title);
		player.startTrack(next, false);
	}
	
	void pause() {
		if (player.getPlayingTrack() != null) {
			player.setPaused(true);
		}
	}
	
	void unpause() {
		player.setPaused(false);
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
