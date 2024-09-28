package commands.admin;

import lib.commands.Command;
import lib.main.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * @author Patrick Ubelhor
 * @version 11/27/2023
 */
public class Unmute extends Command {

	private static final Logger logger = LogManager.getLogger(Unmute.class);

	public Unmute(Permission perm) {
		super("unmute", perm);
	}


	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getMessage().getChannel();
		List<Member> members = event.getMessage().getMentions().getMembers();

		// Make sure the user entered at least one @mention
		if (members.isEmpty()) {
			logger.debug("Did not find any @mentions in message");
			channel.sendMessage("You must @mention 1 or more users to unmute!").queue();
			return;
		}

		// Mute all the members
		for (Member member : members) {
			member.mute(false).queue(
				success -> {
					logger.info("Successfully unmuted {}", member.getEffectiveName());
					channel.sendMessage("Successfully unmuted ")
						.addContent(member.getEffectiveName())
						.addContent("!")
						.queue();
				},

				error -> {
					logger.warn("Error unmuting user: {}\n{}", member.getEffectiveName(), error.toString());
					channel.sendMessage("Error unmuting user: ")
						.addContent(member.getEffectiveName())
						.queue();
				}
			);
		}
	}

	@Override
	public void runSlash(SlashCommandInteractionEvent event) {
		Member member = event.getOption("user").getAsMember();
		assert member != null;

		member.mute(false).queue(
			success -> {
				logger.info("Successfully unmuted {}", member.getEffectiveName());
				event.reply("Successfully unmuted %s!".formatted(member.getEffectiveName())).queue();
			},
			error -> {
				logger.warn("Error unmuting user: {}\n{}", member.getEffectiveName(), error.toString());
				event.reply("Error unmuting user: %s".formatted(member.getEffectiveName())).queue();
			}
		);
	}

	@Override
	public String getUsage() {
		return "unmute @users";
	}

	@Override
	public String getDescription() {
		return "Server unmute the @mentioned users";
	}

	@Override
	public SlashCommandData getCommandData() {
		return super.getDefaultCommandData()
			.addOption(OptionType.USER, "user", "The user to unmute", true);
	}
}
