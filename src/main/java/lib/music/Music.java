package lib.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import lib.commands.Command;
import lib.main.Permission;
import lib.triggers.Trigger;
import main.Bot;
import main.Config;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @author Patrick Ubelhor
 * @version 12/23/2025
 */
public abstract class Music extends Command implements Trigger {
	
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
		return leaveAudioChannel(guildId, msg -> event.getChannel().sendMessage(msg).queue());
	}


	protected final boolean leaveAudioChannel(SlashCommandInteractionEvent event) {
		Long guildId = event.getGuild().getIdLong();
		return leaveAudioChannel(guildId, msg -> event.reply(msg).queue());
	}


	protected final boolean leaveAudioChannel(Long guildId, Consumer<String> handleCannotLeave) {
		if (!audioManagers.containsKey(guildId)) {
			handleCannotLeave.accept("I can't leave a server I'm not in!");
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

	@Override
	public final void onGuildVoiceJoin(@NonNull GuildVoiceUpdateEvent event) {}

	@Override
	public final void onGuildVoiceLeave(@NotNull GuildVoiceUpdateEvent event) {
		AudioChannelUnion channel = event.getChannelLeft();
		if (channel == null) {
			return;
		}

		VoiceChannel vc = channel.asVoiceChannel();
		List<Member> members = vc.getMembers();
		if (members.size() == 1 && members.getFirst().getIdLong() == Bot.getJDA().getSelfUser().getIdLong()) {
			leaveAudioChannel(channel.getGuild().getIdLong(), msg -> {});
		}
	}

	@Override
	public final void onGuildVoiceMove(@NonNull GuildVoiceUpdateEvent event) {
		onGuildVoiceLeave(event);
	}

}
