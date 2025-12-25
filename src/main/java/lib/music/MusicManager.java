package lib.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import jakarta.annotation.Nullable;
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

import java.io.Closeable;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * @author Patrick Ubelhor
 * @version 12/25/2025
 */
public final class MusicManager implements Closeable {

	private static MusicManager instance = new MusicManager();
	public static MusicManager getInstance() {
		return instance;
	}

	private final AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
	private final HashMap<Long, TrackScheduler> trackSchedulers = new HashMap<>();
	private final HashMap<Long, AudioManager> audioManagers = new HashMap<>();

	public MusicManager() {
		YoutubeAudioSourceManager youtubeAudioSourceManager = new YoutubeAudioSourceManager(false);

		audioPlayerManager.registerSourceManager(youtubeAudioSourceManager);
		audioPlayerManager.enableGcMonitoring();
		audioPlayerManager.setFrameBufferDuration(10_000); // 10 seconds
		audioPlayerManager.setPlayerCleanupThreshold(30_000); // 30 seconds

		AudioSourceManagers.registerRemoteSources(
			audioPlayerManager,
			com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class
		);
	}


	public void close() {
		audioPlayerManager.shutdown();
	}


	public boolean joinAudioChannel(Guild guild, Member member, MessageChannel logChannel) {
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
		trackSchedulers.putIfAbsent(guildId, generateTrackScheduler());
		TrackScheduler trackScheduler = trackSchedulers.get(guildId);
		trackScheduler.setLogChannel(logChannel);

		guildAudioManager.setSendingHandler(new AudioPlayerSendHandler(trackScheduler.getPlayer()));
		guildAudioManager.openAudioConnection(vc);
		audioManagers.put(guildId, guildAudioManager);
		return true;
	}


	private TrackScheduler generateTrackScheduler() {
		AudioPlayer player = audioPlayerManager.createPlayer();
		TrackScheduler trackScheduler = new TrackScheduler(player);
		player.setVolume(Config.getConfig().MUSIC_VOLUME());
		player.addListener(trackScheduler); // TODO: How is this not a circular reference?

		return trackScheduler;
	}


	/**
	 * @param guildId The snowflake ID of the guild
	 * @return The track schedule for the guild. `Null` if the bot has never
	 * 	 joined a voice channel for that guild.
	 */
	public @Nullable TrackScheduler getTrackScheduler(Long guildId) {
		return trackSchedulers.get(guildId);
	}


	// TODO: Probably want to add functions like queueNext() and queueLast() to better decouple
	// the implementation from individual commands
	public AudioPlayerManager getAudioPlayerManager() {
		return audioPlayerManager;
	}


	public boolean leaveAudioChannel(MessageReceivedEvent event) {
		Long guildId = event.getGuild().getIdLong();
		return leaveAudioChannel(guildId, msg -> event.getChannel().sendMessage(msg).queue());
	}


	public boolean leaveAudioChannel(SlashCommandInteractionEvent event) {
		Long guildId = event.getGuild().getIdLong();
		return leaveAudioChannel(guildId, msg -> event.reply(msg).queue());
	}


	/**
	 * @param guildId The snowflake ID of the guild containing the voice channel to leave
	 * @param handleCannotLeave A function that consumes a human-readable error message if there is no call to leave
	 * @return True if the bot leaves successfully. Otherwise, false.
	 */
	public boolean leaveAudioChannel(Long guildId, Consumer<String> handleCannotLeave) {
		if (!audioManagers.containsKey(guildId)) {
			handleCannotLeave.accept("I can't leave a server I'm not in!");
			return false;
		}

		AudioManager audioManager = audioManagers.get(guildId);
		audioManager.closeAudioConnection();
		audioManagers.remove(guildId);
		return true;
	}

}
