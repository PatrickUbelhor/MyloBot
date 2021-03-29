package lib.triggers;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;

/**
 * @author Patrick Ubelhor
 * @version 3/28/2021
 */
public interface Trigger {
	
	void onGuildVoiceJoin(GuildVoiceJoinEvent event);
	void onGuildVoiceLeave(GuildVoiceLeaveEvent event);
	void onGuildVoiceMove(GuildVoiceMoveEvent event);
	
}
