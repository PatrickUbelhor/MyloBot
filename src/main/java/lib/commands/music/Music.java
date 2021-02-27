package lib.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import lib.commands.Command;
import main.Globals;
import lib.main.Permission;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Patrick Ubelhor
 * @version 2/24/2021
 */
public abstract class Music extends Command {
	
	protected static AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	private static final AtomicBoolean hasInit = new AtomicBoolean(false);
	protected static HashMap<Long, TrackScheduler> trackSchedulers = new HashMap<>();
	
	
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
		}
		return true;
	}
	
	
	@Override
	protected final boolean subEnd() {
		playerManager.shutdown();
		return true;
	}
	
	
	// TODO: Should return track scheduler. On failure, should throw exception.
	protected final boolean joinAudioChannel(MessageReceivedEvent event) {
		AudioManager guildAudioManager = event.getGuild().getAudioManager();
		VoiceChannel vc = event.getMember().getVoiceState().getChannel();
		
		// Refuses to play if user is not in a voice channel
		if (vc == null) {
			event.getTextChannel().sendMessage("You must be in a voice channel to begin playing music.").queue();
			return false;
		}
		
		Long guildId = event.getGuild().getIdLong();
		trackSchedulers.putIfAbsent(guildId, generatePlayer());
		TrackScheduler trackScheduler = trackSchedulers.get(guildId);
		
		guildAudioManager.setSendingHandler(new AudioPlayerSendHandler(trackScheduler.getPlayer()));
		guildAudioManager.openAudioConnection(vc);
		return true;
	}
	
	
	private TrackScheduler generatePlayer() {
		AudioPlayer player = playerManager.createPlayer();
		TrackScheduler trackScheduler = new TrackScheduler(player);
		player.setVolume(Globals.MUSIC_VOLUME);
		player.addListener(trackScheduler); // TODO: How is this not a circular reference?
		
		return trackScheduler;
	}
	
}
