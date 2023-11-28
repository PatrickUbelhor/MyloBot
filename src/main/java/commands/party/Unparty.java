package commands.party;

import lib.main.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * @author Patrick Ubelhor
 * @version 11/28/2023
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
		return "Disbands the active party in your current voice channel.";
	}
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		MessageChannel textChannel = event.getChannel();
		AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();

		if (audioChannel == null) {
			textChannel.sendMessage("You must be in a voice channel to disband a party").queue();
			return;
		}

		if (!partyExists(audioChannel.getIdLong())) {
			textChannel.sendMessage("This voice channel doesn't have a party.").queue();
			return;
		}
		
		PartyState party = removeParty(audioChannel.getIdLong());
		String response = "Disbanded party '%s'".formatted(party.getName());
		
		textChannel.sendMessage(response).queue();
	}

	@Override
	public void runSlash(SlashCommandInteractionEvent event) {
		AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();

		if (audioChannel == null) {
			event.reply("You must be in a voice channel to disband a party").queue();
			return;
		}

		if (!partyExists(audioChannel.getIdLong())) {
			event.reply("This voice channel doesn't have a party.").queue();
			return;
		}

		PartyState party = removeParty(audioChannel.getIdLong());
		String response = "Disbanded party '%s'".formatted(party.getName());

		event.reply(response).queue();
	}

	@Override
	public SlashCommandData getCommandData() {
		return super.getDefaultCommandData();
	}
	
}
