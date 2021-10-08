package main;

import commands.party.AbstractParty;
import lib.triggers.Trigger;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;

/**
 * @author Patrick Ubelhor
 * @version 9/30/2021
 */
public class PartyTrigger implements Trigger {
	
	@Override
	public final void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
		AbstractParty.handleJoin(event.getChannelJoined(), event.getMember());
	}
	
	
	@Override
	public final void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		AbstractParty.handleLeave(event.getChannelLeft());
	}
	
	
	@Override
	public final void onGuildVoiceMove(GuildVoiceMoveEvent event) {
		AbstractParty.handleLeave(event.getChannelLeft());
		AbstractParty.handleJoin(event.getChannelJoined(), event.getMember());
	}
	
}
