package lib.music.v2;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import main.Bot;
import net.dv8tion.jda.api.entities.Activity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Patrick Ubelhor
 * @version 8/8/2021
 */
public class PlayerEventHandler extends AudioEventAdapter {
	
	private static final Logger logger = LogManager.getLogger(PlayerEventHandler.class);
	
	private List<Runnable> subscribers = new LinkedList<>();
	
	
	public void subscribe(Runnable callback) {
		subscribers.add(callback);
	}
	
	
	private void alertSubscribers() {
		for (Runnable sub : subscribers) {
			sub.run();
		}
	}
	
	
	@Override
	public void onPlayerPause(AudioPlayer player) {}
	
	
	@Override
	public void onPlayerResume(AudioPlayer player) {}
	
	
	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		logger.info("Track has begun");
		player.getPlayingTrack().setPosition(0);
		
		Activity status = Activity.playing(track.getInfo().title);
		Bot.setStatusMessage(status);
	}
	
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		logger.info("Ended");
		Bot.setStatusMessage(null);
		
		if (endReason.mayStartNext) {
			alertSubscribers();
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
		logger.warn("Track threw exception with severity {}", exception.severity.name());
		logger.warn(exception.getMessage());
		
		// TODO: should it play next, or will onTrackEnd handle it?
	}
	
	
	@Override
	public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
		logger.warn("Track is stuck with threshold {}ms", thresholdMs);
		alertSubscribers();
	}
	
}
