package commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import commands.Command;

/**
 * @author PatrickUbelhor
 * @version 05/27/2017
 *
 * TODO: Make bot enter the voice channel of the requester
 * TODO: Make bot leave voice channel after some period of inactivity
 * TODO: Add responses to user interaction
 * TODO: Add song queueing
 * TODO: Add song skipping
 * TODO: What if a user in a separate voice channel requests the bot?
 * TODO: Move contents of 'music' package back into this file? I'll need to make a "!Skip" command anyway
 */
abstract class Music extends Command {
	
	protected static AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	protected static AudioPlayer player = playerManager.createPlayer();
	protected static TrackScheduler trackScheduler = new TrackScheduler(player);
	private static boolean hasInit = false;
	
	@Override
	protected boolean subInit() {
		if (!hasInit) {
			AudioSourceManagers.registerRemoteSources(playerManager);
			AudioSourceManagers.registerLocalSource(playerManager);
		}
		hasInit = true;
		return true;
	}
	
}
