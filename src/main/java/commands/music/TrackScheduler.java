package commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author PatrickUbelhor
 * @version 05/28/2017
 *
 * TODO: Add in responses from bot
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
			System.out.println("Adding to queue");
			queue.offer(track);
		}
	}
	
	void playNext() {
		System.out.print("Playing next track: ");
		AudioTrack next = queue.poll();
		System.out.println(next == null ? "end of queue" : next.getInfo().title);
		player.startTrack(next, false);
	}
	
	void pause() {
		player.setPaused(true);
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
		System.out.println("Track has begun");
		player.getPlayingTrack().setPosition(0);
	}
	
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		System.out.println("Ended");
		
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
		System.out.println("Threw exception");
	}
	
	@Override
	public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
		System.out.println("Track is stuck");
		playNext();
	}
}
