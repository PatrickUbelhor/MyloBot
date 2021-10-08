package commands.party;

import lib.commands.Command;
import lib.main.Permission;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Patrick Ubelhor
 * @version 9/30/2021
 */
public abstract class AbstractParty extends Command {
	
	private static final String MESSAGE_FORMAT = """
		This call is currently watching/playing %s.
		You can stay, but please remain muted to avoid being a distraction.
		Thanks!
		""";
	
	private static final HashMap<Long, PartyState> parties = new HashMap<>();
	
	public AbstractParty(String name, Permission permission) {
		super(name, permission);
	}
	
	
	protected final boolean partyExists(Long voiceChannelId) {
		return parties.containsKey(voiceChannelId);
	}
	
	
	protected final void createParty(Long voiceChannelId, String partyName, List<Long> members) {
		PartyState party = new PartyState(partyName, members);
		parties.put(voiceChannelId,  party);
	}
	
	
	protected final PartyState removeParty(Long voiceChannelId) {
		return parties.remove(voiceChannelId);
	}
	
	
	// If not a member of the party, then send them a message
	public static void handleJoin(VoiceChannel vc, Member member) {
		Long voiceId = vc.getIdLong();
		if (parties.containsKey(voiceId)) {
			PartyState party = parties.get(voiceId);
			Long userId = member.getIdLong();
			
			if (!party.getNotifiedMembers().contains(userId)) {
				String message = MESSAGE_FORMAT.formatted(party.getName());
				member.getUser()
						.openPrivateChannel()
						.flatMap(channel -> channel.sendMessage(message))
						.queue();
				
				party.addNotifiedMember(userId);
			}
		}
	}
	
	
	// If all original members have left, then destroy the party
	public static void handleLeave(VoiceChannel vc) {
		Long voiceId = vc.getIdLong();
		if (parties.containsKey(voiceId)) {
			PartyState party = parties.get(voiceId);
			
			// Get list of user IDs for members currently in call
			Set<Long> currentMembers = vc.getMembers() // We fetch list from API in case we missed a leave event (bot went offline)
					.stream()
					.map(ISnowflake::getIdLong)
					.collect(Collectors.toSet());
			
			// Is active if any of the original members is still in call
			boolean isActive = party.getOriginalMembers()
					.stream()
					.anyMatch(currentMembers::contains);
			
			if (!isActive) {
				parties.remove(voiceId);
			}
		}
	}
	
}
