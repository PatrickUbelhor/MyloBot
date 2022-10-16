package commands.party;

import lib.main.Permission;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Patrick Ubelhor
 * @version 10/16/2022
 */
public class Party extends AbstractParty {
	
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
		String partyName = Arrays.stream(Arrays.copyOfRange(args, 1, args.length))
				.reduce("", (s, s2) -> s + " " + s2);
		
		if (vc == null) {
			textChannel.sendMessage("You must be in a voice channel to create a party.").queue();
			return;
		}
		
		if (partyExists(vc.getIdLong())) {
			textChannel.sendMessage("This voice channel already has a party.").queue();
			return;
		}
		
		List<Long> members = vc.getMembers()
				.stream()
				.map(ISnowflake::getIdLong)
				.collect(Collectors.toList());
		
		createParty(vc.getIdLong(), partyName, members);
		textChannel.sendMessage("Created party '%s'".formatted(partyName)).queue();
	}
	
	
	@Override
	public String getUsage() {
		return this.getName() + " <party_name>";
	}
	
	
	@Override
	public String getDescription() {
		return "Creates a party in the voice chat and notifies anyone who joins to be quiet." +
			"A single voice channel may only have one active party at a time.";
	}
	
	
	@Override
	public CommandData getCommandData() {
		return super.getCommandData()
			.addOption(
				OptionType.STRING,
				"party_name",
				"The name of the party. This name will be used to inform anyone that joins what is going on.",
				true
			);
	}
	
}
