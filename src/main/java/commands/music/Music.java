package commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import commands.Command;
import main.Globals;
import main.Permission;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Patrick Ubelhor
 * @version 2/7/2019
 */
abstract class Music extends Command {
	
	static AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	static AudioPlayer player = playerManager.createPlayer();
	static TrackScheduler trackScheduler = new TrackScheduler(player);
	private static AtomicBoolean hasInit = new AtomicBoolean(false);
	
	protected Music(String name) {
		super(name);
	}
	
	protected Music(String name, Permission perm) {
		super(name, perm);
	}
	
	
	@Override
	protected boolean subInit() {
		if (!hasInit.getAndSet(true)) { // Each music command (play, skip, etc.) will call this. Only want to run it once.
			playerManager.enableGcMonitoring();
			playerManager.setFrameBufferDuration(10000); // 10 seconds
			playerManager.setPlayerCleanupThreshold(30000); // 30 seconds
			AudioSourceManagers.registerRemoteSources(playerManager);
			player.setVolume(Globals.MUSIC_VOLUME);
			player.addListener(trackScheduler);
			
		}
		return true;
	}
	
	
	@Override
	protected final boolean subEnd() {
		playerManager.shutdown();
		return true;
	}


	protected final boolean joinAudioChannel(MessageReceivedEvent event) {
		AudioManager am = event.getGuild().getAudioManager();
		VoiceChannel vc = event.getMember().getVoiceState().getChannel();

		// Refuses to play if user is not in a voice channel
		if (vc == null) {
			event.getTextChannel().sendMessage("You must be in a voice channel to begin playing music.").queue();
			return false;
		}

		am.setSendingHandler(new AudioPlayerSendHandler(player));
		am.openAudioConnection(vc);

		return true;
	}
	
}
