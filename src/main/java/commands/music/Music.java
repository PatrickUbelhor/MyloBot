package commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import commands.Command;

/**
 * @author PatrickUbelhor
 * @version 05/28/2017
 *
 * TODO: Add responses to user interaction
 */
abstract class Music extends Command {
	
	static AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	static AudioPlayer player = playerManager.createPlayer();
	static TrackScheduler trackScheduler = new TrackScheduler(player);
	private static boolean hasInit = false;
	
	@Override
	protected boolean subInit() {
		if (!hasInit) {
			AudioSourceManagers.registerRemoteSources(playerManager);
			AudioSourceManagers.registerLocalSource(playerManager);
			player.addListener(trackScheduler);
		}
		hasInit = true;
		return true;
	}
	
}
