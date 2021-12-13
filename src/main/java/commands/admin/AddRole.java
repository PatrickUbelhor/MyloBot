package commands.admin;

import lib.commands.Command;
import lib.main.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * @author Patrick Ubelhor
 * @version 12/13/2021
 */
public class AddRole extends Command {
	
	private static final Logger logger = LogManager.getLogger(AddRole.class);
	
	public AddRole(Permission perm) {
		super("addRole", perm);
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		User author = event.getAuthor();
		List<Member> members = event.getMessage().getMentionedMembers();
		Guild guild = event.getGuild();
		TextChannel channel = event.getTextChannel();
		Member self = guild.getSelfMember();
		
		if (args.length < 3) {
			channel.sendMessage("Usage: " + this.getUsage()).queue();
			return;
		}
		
		// Check if any users were @mentioned
		if (members.isEmpty()) {
			channel.sendMessage("You must @mention 1 or more users to modify.").queue();
			return;
		}
		
		// Check if we have permission to assign roles in this guild
		if (!self.hasPermission(net.dv8tion.jda.api.Permission.MANAGE_ROLES)) {
			channel.sendMessage("I don't have permission to assign roles in this guild.").queue();
			return;
		}
		
		// Returns a list of roles (can have multiple matches due to case sensitivity
		String roleName = args[1];
		List<Role> possibleRoles = guild.getRolesByName(roleName, true);
		if (possibleRoles.isEmpty()) {
			channel.sendMessage("I can't find a role with the name '%s'".formatted(roleName)).queue();
		}
		
		// Check if we are higher in the role hierarchy than the target role.
		// Can't add roles equal or higher than us.
		Role role = possibleRoles.get(0);
		logger.debug("Found role: {} | {}", role.getName(), role.getId());
		if (!self.canInteract(role)) {
			channel.sendMessage("I'm not ranked high enough to add that role.").queue();
			return;
		}
		
		// Assign role to all mentioned users
		for (Member member : members) {
			guild.addRoleToMember(member, role)
					.reason("Requested by " + author.getAsTag())
					.queue();
		}
	}
	
	
	@Override
	public String getUsage() {
		return "addRole <role name> @users";
	}
	
	@Override
	public String getDescription() {
		return "Adds a role to a user";
	}
	
}
