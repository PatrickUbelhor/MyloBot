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
 * @version 11/28/2023
 */
public class Kick extends Command {

	private static final Logger logger = LogManager.getLogger(Kick.class);

	public Kick(Permission perm) {
		super("kick", perm);
	}


	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		List<Member> members = event.getMessage().getMentions().getMembers();
		Guild guild = event.getGuild();
		MessageChannel channel = event.getChannel();
		Member self = guild.getSelfMember();

		// Check to see if any users were @mentioned
		if (members.isEmpty()) {
			logger.debug("Did not find any @mentions in message");
			channel.sendMessage("You must @mention 1 or more users to kick!").queue();
			return;
		}

		// Check if we have permission to kick users from this guild
		if (!self.hasPermission(net.dv8tion.jda.api.Permission.KICK_MEMBERS)) {
			logger.debug("Insufficient permissions to kick users");
			channel.sendMessage("I don't have permission to kick users from this guild!").queue();
			return;
		}

		// Go through the list of members and try to kick them
		for (Member member : members) {

			// Check if we are higher in the role hierarchy than the member. Can't kick members equal to or above us.
			if (!self.canInteract(member)) {
				logger.debug("Can't kick {} due to hierarchy restriction", member.getEffectiveName());
				channel.sendMessage("I'm not ranked high enough to kick ")
					.addContent(member.getEffectiveName())
					.addContent("!")
					.queue();

				continue; // Move on to next user
			}


			// Finally kick the member
			guild.kick(member).queue(
				success -> {
					logger.info("Successfully kicked {}", member.getEffectiveName());
					channel.sendMessage("Successfully kicked ")
						.addContent(member.getEffectiveName())
						.addContent("!")
						.queue();
				},
				error -> {
					logger.warn("Error kicking user: {}\n{}", member.getEffectiveName(), error.toString());
					channel.sendMessage("Error kicking user: ")
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

		member.kick().queue(
			success -> {
				logger.info("Successfully kicked {}", member.getEffectiveName());
				event.reply("Successfully kicked %s!".formatted(member.getEffectiveName())).queue();
			},
			error -> {
				logger.warn("Error kicking user: {}\n{}", member.getEffectiveName(), error.toString());
				event.reply("Error kicking user: %s".formatted(member.getEffectiveName())).queue();
			}
		);
	}

	@Override
	public String getUsage() {
//		return "kick @user [reason]";
		return "kick @users";
	}

	@Override
	public String getDescription() {
		return "Kicks a user from the guild";
	}

	@Override
	public SlashCommandData getCommandData() {
		return super.getDefaultCommandData()
			.addOption(OptionType.USER, "user", "The user to kick", true);
	}
}
