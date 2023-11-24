package commands.party;

import lib.main.Permission;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

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

		MessageChannel textChannel = event.getChannel();
		AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
		String partyName = Arrays.stream(Arrays.copyOfRange(args, 1, args.length))
			.reduce("", (s, s2) -> s + " " + s2);

		if (audioChannel == null) {
			textChannel.sendMessage("You must be in a voice channel to create a party.").queue();
			return;
		}

		if (partyExists(audioChannel.getIdLong())) {
			textChannel.sendMessage("This voice channel already has a party.").queue();
			return;
		}

		List<Long> members = audioChannel.getMembers()
			.stream()
			.map(ISnowflake::getIdLong)
			.collect(Collectors.toList());

		createParty(audioChannel.getIdLong(), partyName, members);
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
	public SlashCommandData getCommandData() {
		return super.getCommandData()
			.addOption(
				OptionType.STRING,
				"party_name",
				"The name of the party. This name will be used to inform anyone that joins what is going on.",
				true
			);
	}

}
