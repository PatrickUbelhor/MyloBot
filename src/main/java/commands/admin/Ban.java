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
public class Ban extends Command {
	
	public Ban(Permission perm) {
		super("ban", perm);
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
			channel.sendMessage("You must @mention 1 or more users to ban!").queue();
			return;
		}
		
		// Check if we have permission to ban users from this guild
		if (!self.hasPermission(net.dv8tion.jda.core.Permission.BAN_MEMBERS)) {
			logger.debug("Insufficient permissions to ban users");
			channel.sendMessage("I don't have permission to ban users from this guild!").queue();
			return;
		}
		
		// Go through the list of members and try to ban them
		for (Member member : members) {
			
			// Check if we are higher in the role hierarchy than the member. Can't kick members equal to or above us.
			if (!self.canInteract(member)) {
				logger.debug("Can't ban " + member.getEffectiveName() + " due to hierarchy restriction");
				channel.sendMessage("I'm not ranked high enough to ban ")
						.append(member.getEffectiveName())
						.append("!")
						.queue();
				
				continue; // Move on to next user
			}
			
			
			// TODO: Implement delDays argument: delete messages from N days ago from this user
			// Finally ban the member
			controller.ban(member, 0).queue(
					success -> {
						logger.info("Successfully banned " + member.getEffectiveName());
						channel.sendMessage("Successfully banned ").append(member.getEffectiveName()).append("!").queue();
					},
					
					error -> {
						String errorMsg = String.format("Error banning user: %s\n%s", member.getEffectiveName(), error.toString());
						logger.warn(errorMsg);
						channel.sendMessage("Error banning user: ").append(member.getEffectiveName()).queue();
					}
			);
		}
	}
	
	
	@Override
	public String getUsage() {
//		return "ban @user [reason]";
<<<<<<< HEAD
		return "ban @user1 @user2 ...";
=======
		return "ban @users";
>>>>>>> dev
	}
	
	
	@Override
	public String getDescription() {
		return "Bans a user from the guild";
	}
}
