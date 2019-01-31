package commands.admin;

import commands.Command;
import main.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.List;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 6/12/2018
 */
public class Kick extends Command {

	public Kick(Permission perm) {
		super("kick", perm);
	}


	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		List<Member> members = event.getMessage().getMentionedMembers();
		Guild guild = event.getGuild();
		GuildController controller = guild.getController();
		TextChannel channel = event.getTextChannel();
		Member self = guild.getSelfMember();

		// Check to see if any users were @mentioned
		if (members.isEmpty()) {
			logger.debug("Did not find any @mentions in message");
			channel.sendMessage("You must @mention 1 or more users to kick!").queue();
			return;
		}

		// Check if we have permission to kick users from this guild
		if (!self.hasPermission(net.dv8tion.jda.core.Permission.KICK_MEMBERS)) {
			logger.debug("Insufficient permissions to kick users");
			channel.sendMessage("I don't have permission to kick users from this guild!").queue();
			return;
		}

		// Go through the list of members and try to kick them
		for (Member member : members) {

			// Check if we are higher in the role hierarchy than the member. Can't kick members equal to or above us.
			if (!self.canInteract(member)) {
				logger.debug("Can't kick " + member.getEffectiveName() + " due to hierarchy restriction");
				channel.sendMessage("I'm not ranked high enough to kick ")
						.append(member.getEffectiveName())
						.append("!")
						.queue();

				continue; // Move on to next user
			}


			// Finally kick the member
			controller.kick(member).queue(
					success -> {
						logger.info("Successfully kicked " + member.getEffectiveName());
						channel.sendMessage("Successfully kicked ").append(member.getEffectiveName()).append("!").queue();
					},

					error -> {
						String errorMsg = String.format("Error kicking user: %s\n%s", member.getEffectiveName(), error.toString());
						logger.warn(errorMsg);
						channel.sendMessage("Error kicking user: ").append(member.getEffectiveName()).queue();
					}
			);
		}
	}


	@Override
	public String getUsage() {
//		return "kick @user [reason]";
<<<<<<< HEAD
		return "kick @user1 @user2 ...";
=======
		return "kick @users";
>>>>>>> dev
	}


	@Override
	public String getDescription() {
		return "Kicks a user from the guild";
	}
}
