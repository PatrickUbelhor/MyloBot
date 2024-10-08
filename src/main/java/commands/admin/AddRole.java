package commands.admin;

import lib.commands.Command;
import lib.main.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
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
public class AddRole extends Command {

	private static final Logger logger = LogManager.getLogger(AddRole.class);

	public AddRole(Permission perm) {
		super("addRole", perm);
	}


	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		User author = event.getAuthor();
		List<Member> members = event.getMessage().getMentions().getMembers();
		Guild guild = event.getGuild();
		MessageChannel channel = event.getChannel();
		Member self = guild.getSelfMember();

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

		// Get role if @mentioned, else get role by name
		List<Role> possibleRoles;
		if (event.getMessage().getMentions().getRoles().isEmpty()) {
			// Returns a list of roles (can have multiple matches due to case sensitivity
			String roleName = args[1];
			possibleRoles = guild.getRolesByName(roleName, true);

			if (possibleRoles.isEmpty()) {
				channel.sendMessage("I can't find a role with the name '%s'".formatted(roleName)).queue();
				return;
			}
		} else {
			possibleRoles = event.getMessage().getMentions().getRoles();
		}

		// Check if we are higher in the role hierarchy than the target role.
		// Can't add roles equal or higher than us.
		Role role = possibleRoles.getFirst();
		logger.debug("Found role: {} | {}", role.getName(), role.getId());
		if (!self.canInteract(role)) {
			channel.sendMessage("I'm not ranked high enough to add that role.").queue();
			return;
		}

		// Assign role to all mentioned users
		for (Member member : members) {
			guild.addRoleToMember(member, role)
				.reason("Requested by " + author.getName())
				.queue();
		}
	}

	@Override
	public void runSlash(SlashCommandInteractionEvent event) {
		Guild guild = event.getGuild();
		Member self = guild.getSelfMember();

		if (!self.hasPermission(net.dv8tion.jda.api.Permission.MANAGE_ROLES)) {
			event.reply("I don't have permission to assign roles in this guild.").queue();
			return;
		}

		Member member = event.getOption("user").getAsMember();
		Role role = event.getOption("role").getAsRole();

		if (!self.canInteract(role)) {
			event.reply("I'm not ranked high enough to add that role.").queue();
			return;
		}

		guild.addRoleToMember(member, role)
			.reason("Requested by " + event.getUser().getName())
			.queue();
	}

	@Override
	public String getUsage() {
		return "addRole <role name> @users";
	}

	@Override
	public String getDescription() {
		return "Adds a role to a user";
	}

	@Override
	public SlashCommandData getCommandData() {
		return super.getDefaultCommandData()
			.addOption(OptionType.USER, "user", "The user on which to apply the role", true)
			.addOption(OptionType.ROLE, "role", "The role to add to the user", true);
	}
}
