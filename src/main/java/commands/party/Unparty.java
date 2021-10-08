package commands.party;

import lib.main.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author Patrick Ubelhor
 * @version 9/30/2021
 */
public class Unparty extends AbstractParty {
	
	public Unparty(Permission permission) {
		super("unparty", permission);
	}
	
	@Override
	public String getUsage() {
		return this.getName();
	}
	
	@Override
	public String getDescription() {
		return "Disbands an active party";
	}
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		TextChannel textChannel = event.getTextChannel();
		VoiceChannel vc = event.getMember().getVoiceState().getChannel();
		
		if (vc == null) {
			textChannel.sendMessage("You must be in a voice channel to disband a party").queue();
			return;
		}
		
		if (!partyExists(vc.getIdLong())) {
			textChannel.sendMessage("This voice channel doesn't have a party.").queue();
			return;
		}
		
		PartyState party = removeParty(vc.getIdLong());
		String response = String.format("Disbanded party '%s'.", party.getName());
		
		textChannel.sendMessage(response).queue();
	}
	
}
