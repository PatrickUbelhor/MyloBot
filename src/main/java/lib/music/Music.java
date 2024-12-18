package lib.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import lib.commands.Command;
import lib.main.Permission;
import main.Config;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Patrick Ubelhor
 * @version 12/6/2024
 */
public abstract class Music extends Command {
	
	protected static AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	private static final AtomicBoolean hasInit = new AtomicBoolean(false);
	protected static final HashMap<Long, TrackScheduler> trackSchedulers = new HashMap<>();
	private static final HashMap<Long, AudioManager> audioManagers = new HashMap<>();
	
	
	protected Music(String name, Permission perm) {
		super(name, perm);
	}


	@Override
	protected boolean subInit() {
		if (!hasInit.getAndSet(true)) { // Each music command (play, skip, etc.) will call this. Only want to run it once.
			dev.lavalink.youtube.YoutubeAudioSourceManager youtubeSourceManager =
				new dev.lavalink.youtube.YoutubeAudioSourceManager(false);

			playerManager.registerSourceManager(youtubeSourceManager);
			playerManager.enableGcMonitoring();
			playerManager.setFrameBufferDuration(10000); // 10 seconds
			playerManager.setPlayerCleanupThreshold(30000); // 30 seconds

			AudioSourceManagers.registerRemoteSources(
				playerManager,
				com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class
			);
		}
		return true;
	}


	@Override
	protected final boolean subEnd() {
		playerManager.shutdown();
		return true;
	}


	// TODO: Should return track scheduler. On failure, should throw exception.
	protected final boolean joinAudioChannel(Guild guild, Member member, MessageChannel logChannel) {
		AudioManager guildAudioManager = guild.getAudioManager();
		AudioChannelUnion audioChannel = member.getVoiceState().getChannel();

		// Refuses to play if user is not in a voice channel
		if (audioChannel == null || audioChannel.getType() != ChannelType.VOICE) {
			return false;
		}

		VoiceChannel vc = audioChannel.asVoiceChannel();
		if (guildAudioManager.isConnected() && guildAudioManager.getConnectedChannel().getIdLong() == vc.getIdLong()) {
			return true;
		}

		Long guildId = guild.getIdLong();
		trackSchedulers.putIfAbsent(guildId, generatePlayer());
		TrackScheduler trackScheduler = trackSchedulers.get(guildId);
		trackScheduler.setLogChannel(logChannel);

		guildAudioManager.setSendingHandler(new AudioPlayerSendHandler(trackScheduler.getPlayer()));
		guildAudioManager.openAudioConnection(vc);
		audioManagers.put(guildId, guildAudioManager);
		return true;
	}


	protected final boolean leaveAudioChannel(MessageReceivedEvent event) {
		Long guildId = event.getGuild().getIdLong();

		if (!audioManagers.containsKey(guildId)) {
			event.getChannel().sendMessage("I can't leave a server I'm not in!").queue();
			return false;
		}

		AudioManager audioManager = audioManagers.get(guildId);
		audioManager.closeAudioConnection();
		audioManagers.remove(guildId);
		return true;
	}


	protected final boolean leaveAudioChannel(SlashCommandInteractionEvent event) {
		Long guildId = event.getGuild().getIdLong();

		if (!audioManagers.containsKey(guildId)) {
			event.reply("I can't leave a server I'm not in!").queue();
			return false;
		}

		AudioManager audioManager = audioManagers.get(guildId);
		audioManager.closeAudioConnection();
		audioManagers.remove(guildId);
		return true;
	}


	private TrackScheduler generatePlayer() {
		AudioPlayer player = playerManager.createPlayer();
		TrackScheduler trackScheduler = new TrackScheduler(player);
		player.setVolume(Config.getConfig().MUSIC_VOLUME());
		player.addListener(trackScheduler); // TODO: How is this not a circular reference?

		return trackScheduler;
	}

}
