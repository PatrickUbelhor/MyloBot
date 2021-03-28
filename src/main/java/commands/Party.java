package commands;

import lib.commands.Command;
import lib.main.Permission;
import lib.triggers.Trigger;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Patrick Ubelhor
 * @version 3/28/2021
 */
public class Party extends Command implements Trigger {
	
	private static final String MESSAGE_FORMAT =
			"This call is currently watching/playing %s. " +
			"You can stay, but please remain muted to avoid being a distraction. " +
			"Thanks!";
	
	private HashMap<Long, PartyState> parties = new HashMap<>();
	
	
	public Party(Permission permission) {
		super("party", permission);
	}
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		if (args.length < 2) {
			event.getChannel().sendMessage("Usage: " + getUsage()).queue();
			return;
		}
		
		TextChannel textChannel = event.getTextChannel();
		VoiceChannel vc = event.getMember().getVoiceState().getChannel();
		String partyName = args[1];
		
		if (vc == null) {
			textChannel.sendMessage("You must be in a voice channel to create a party.").queue();
			return;
		}
		
		if (parties.containsKey(vc.getIdLong())) {
			textChannel.sendMessage("This voice channel already has a party.").queue();
			return;
		}
		
		
		List<Long> members = vc.getMembers()
				.stream()
				.map(ISnowflake::getIdLong)
				.collect(Collectors.toList());
		
		PartyState party = new PartyState(partyName, members);
		parties.put(vc.getIdLong(), party);
	}
	
	
	@Override
	public String getUsage() {
		return this.getName() + " <party_name>";
	}
	
	
	@Override
	public String getDescription() {
		return "Creates a party in the voice chat and notifies anyone who joins to be quiet";
	}
	
	
	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
		handleJoin(event.getChannelJoined(), event.getMember());
	}
	
	
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		handleLeave(event.getChannelLeft(), event.getMember());
	}
	
	
	@Override
	public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
		handleLeave(event.getChannelLeft(), event.getMember());
		handleJoin(event.getChannelJoined(), event.getMember());
	}
	
	
	// If not a member of the party, then send them a message
	private void handleJoin(VoiceChannel vc, Member member) {
		Long voiceId = vc.getIdLong();
		if (parties.containsKey(voiceId)) {
			PartyState party = parties.get(voiceId);
			Long userId = member.getIdLong();
			
			if (!party.currentMembers.contains(userId)) {
				String message = String.format(MESSAGE_FORMAT, parties.get(voiceId).name);
				member.getUser()
						.openPrivateChannel()
						.flatMap(channel -> channel.sendMessage(message))
						.queue();
				
				party.currentMembers.add(userId);
			}
		}
	}
	
	
	private void handleLeave(VoiceChannel vc, Member member) {
		// TODO: Destroy party once everyone leaves?
	}
	
	
	private static class PartyState {
		private final String name;
		private final HashSet<Long> originalMembers;
		private final HashSet<Long> currentMembers;
		
		private PartyState(String name, Collection<Long> members) {
			this.name = name;
			this.originalMembers = new HashSet<>(members);
			this.currentMembers = new HashSet<>(members);
		}
	}
}
