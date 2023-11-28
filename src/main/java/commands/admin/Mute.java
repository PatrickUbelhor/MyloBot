package commands.admin;

import lib.commands.Command;
import lib.main.Permission;
import net.dv8tion.jda.api.entities.Guild;
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
public class Mute extends Command {

	private static final Logger logger = LogManager.getLogger(Mute.class);

	public Mute(Permission perm) {
		super("mute", perm);
	}


	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		Guild guild = event.getGuild();
		MessageChannel channel = event.getMessage().getChannel();
		List<Member> members = event.getMessage().getMentions().getMembers();

		// Make sure the user entered at least one @mention
		if (members.isEmpty()) {
			logger.debug("Did not find any @mentions in message");
			channel.sendMessage("You must @mention 1 or more users to mute!").queue();
			return;
		}

		// Mute all the members
		for (Member member : members) {
			guild.mute(member, true).queue(
				success -> {
					logger.info("Successfully muted " + member.getEffectiveName());
					channel.sendMessage("Successfully muted ")
						.addContent(member.getEffectiveName())
						.addContent("!")
						.queue();
				},

				// FIXME: this doesn't actually do any exception handling
				error -> {
					logger.warn("Error muting user: {}\n{}", member.getEffectiveName(), error.toString());
					channel.sendMessage("Error muting user: ")
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

		member.mute(true).queue(
			success -> {
				logger.info("Successfully muted {}", member.getEffectiveName());
				event.reply("Successfully muted %s!".formatted(member.getEffectiveName())).queue();
			},
			error -> {
				logger.warn("Error muting user: {}\n{}", member.getEffectiveName(), error.toString());
				event.reply("Error muting user: %s".formatted(member.getEffectiveName())).queue();
			}
		);
	}

	@Override
	public String getUsage() {
		return "mute @users";
	}

	@Override
	public String getDescription() {
		return "Server mute the @mentioned users";
	}

	@Override
	public SlashCommandData getCommandData() {
		return super.getDefaultCommandData()
			.addOption(OptionType.USER, "user", "The user to mute", true);
	}
}
