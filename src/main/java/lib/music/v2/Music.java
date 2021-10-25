package lib.music.v2;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import main.Globals;

/**
 * @author Patrick Ubelhor
 * @version 8/10/2021
 */
public class Music {
	
	protected static AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	
	
	private TrackScheduler generatePlayer() {
		AudioPlayer player = playerManager.createPlayer();
		PlayerEventHandler eventHandler = new PlayerEventHandler();
		TrackScheduler trackScheduler = new TrackScheduler(player);
		
		player.setVolume(Globals.MUSIC_VOLUME);
		player.addListener(eventHandler);
		trackScheduler.listen(eventHandler);
		
		return trackScheduler;
	}
	
}
