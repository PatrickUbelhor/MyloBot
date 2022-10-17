package commands.admin;

import lib.commands.Command;
import lib.main.Permission;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Patrick Ubelhor
 * @version 10/16/2022
 */
public class WhoIs extends Command {
	
	private static final Logger logger = LogManager.getLogger(WhoIs.class);
	
	// Format string for each users' information
	private static final String format = """
			   User: %s#%s
			     ID: %s
			   Name: %s
			Created: %s
			 Joined: %s
			 Status: %s
			 Avatar: %s
			  Roles: %s
			""";
	
	public WhoIs(Permission perm) {
		super("whois", perm);
	}

	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getMessage().getChannel();
		List<Member> members = event.getMessage().getMentionedMembers();
		List<Role> roles = event.getMessage().getMentionedRoles();
		
		// Make sure the user entered at least one @mention
		if (members.isEmpty() && roles.isEmpty()) {
			logger.debug("Did not find any @mentions in message");
			channel.sendMessage("You must @mention 1 or more users/roles to identify!").queue();
			return;
		}
		
		// Add all members in mentioned roles
		Set<Member> uniqueMembers = new HashSet<>(members);
		for (Role role : roles) {
			uniqueMembers.addAll(event.getGuild().getMembersWithRoles(role));
		}
		
		// Send a message of information for each user requested
		List<Message> memberInfo = getInfoAboutMembers(uniqueMembers);
		for (Message msg : memberInfo) {
			channel.sendMessage(msg).queue();
		}
	}
	
	@Override
	public void runSlash(SlashCommandEvent event) {
		Member member = null;
		for (OptionMapping option : event.getOptionsByName("user")) {
			member = option.getAsMember();
		}
		
		assert member != null;
		getInfoAboutMembers(List.of(member))
			.forEach(message -> event.reply(message).queue());
	}
	
	private List<Message> getInfoAboutMembers(Collection<Member> members) {
		return members.parallelStream()
			.map(member -> {
				User user = member.getUser();
				return String.format(
					format,
					user.getName(), user.getDiscriminator(),
					user.getId(),
					member.getEffectiveName(),
					user.getTimeCreated(),
					member.getTimeJoined(),
					member.getOnlineStatus().getKey(),
					user.getEffectiveAvatarUrl(),
					rolesToString(member.getRoles())
				);
			})
			.map(info -> new MessageBuilder()
				.appendCodeBlock(info, "yaml")
				.build()
			)
			.toList();
	}
	
	/**
	 * Converts a list of roles into a comma-space separated string.
	 *
	 * @param roles A list of roles.
	 * @return A string representing the list of roles.
	 */
	private String rolesToString(List<Role> roles) {
		StringBuilder sb = new StringBuilder();
		
		for (Role role : roles) {
			sb.append(role.getName());
			sb.append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		
		return sb.toString();
	}

	@Override
	public String getUsage() {
		return "whois @users";
	}
	
	@Override
	public String getDescription() {
		return "Gives information about the target users";
	}
	
	public String getShortDescription() {
		return "Gives information about the target user";
	}
	
	@Override
	public CommandData getCommandData() {
		return super.getDefaultCommandData(getShortDescription())
			.addOption(OptionType.USER, "user", "The user to lookup", true);
	}
	
}
