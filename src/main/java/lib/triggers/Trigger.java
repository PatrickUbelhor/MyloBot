package lib.triggers;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;

/**
 * @author Patrick Ubelhor
 * @version 11/23/2023
 */
public interface Trigger {
	
	void onGuildVoiceJoin(GuildVoiceUpdateEvent event);
	void onGuildVoiceLeave(GuildVoiceUpdateEvent event);
	void onGuildVoiceMove(GuildVoiceUpdateEvent event);
	
}
