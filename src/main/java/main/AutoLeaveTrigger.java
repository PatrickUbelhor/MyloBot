package main;

import jakarta.annotation.Nonnull;
import lib.music.MusicManager;
import lib.triggers.Trigger;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;

import java.util.List;

/**
 * @author Patrick Ubelhor
 * @version 12/24/2025
 */
public class AutoLeaveTrigger implements Trigger {

	@Override
	public final void onGuildVoiceJoin(@Nonnull GuildVoiceUpdateEvent event) {}

	@Override
	public final void onGuildVoiceLeave(@Nonnull GuildVoiceUpdateEvent event) {
		AudioChannelUnion channel = event.getChannelLeft();
		if (channel == null) {
			return;
		}

		VoiceChannel vc = channel.asVoiceChannel();
		List<Member> members = vc.getMembers();
		if (members.size() == 1 && members.getFirst().getIdLong() == Bot.getJDA().getSelfUser().getIdLong()) {
			MusicManager
				.getInstance()
				.leaveAudioChannel(channel.getGuild().getIdLong(), msg -> {});
		}
	}

	@Override
	public final void onGuildVoiceMove(@Nonnull GuildVoiceUpdateEvent event) {
		onGuildVoiceLeave(event);
	}

}
