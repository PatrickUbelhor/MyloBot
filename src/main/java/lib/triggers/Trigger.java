package lib.triggers;

import jakarta.annotation.Nonnull;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;

/**
 * @author Patrick Ubelhor
 * @version 11/23/2023
 */
public interface Trigger {
	
	void onGuildVoiceJoin(@Nonnull GuildVoiceUpdateEvent event);
	void onGuildVoiceLeave(@Nonnull GuildVoiceUpdateEvent event);
	void onGuildVoiceMove(@Nonnull GuildVoiceUpdateEvent event);
	
}
