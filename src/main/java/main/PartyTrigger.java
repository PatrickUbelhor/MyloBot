package main;

import commands.party.AbstractParty;
import lib.triggers.Trigger;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;

/**
 * @author Patrick Ubelhor
 * @version 9/30/2021
 */
public class PartyTrigger implements Trigger {
	
	@Override
	public final void onGuildVoiceJoin(GuildVoiceUpdateEvent event) {
		AbstractParty.handleJoin(event.getChannelJoined(), event.getMember());
	}
	
	
	@Override
	public final void onGuildVoiceLeave(GuildVoiceUpdateEvent event) {
		AbstractParty.handleLeave(event.getChannelLeft());
	}
	
	
	@Override
	public final void onGuildVoiceMove(GuildVoiceUpdateEvent event) {
		AbstractParty.handleLeave(event.getChannelLeft());
		AbstractParty.handleJoin(event.getChannelJoined(), event.getMember());
	}
	
}
