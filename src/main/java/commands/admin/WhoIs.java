package commands.admin;

import lib.commands.Command;
import lib.main.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Patrick Ubelhor
 * @version 11/27/2023
 */
public class WhoIs extends Command {

	private static final Logger logger = LogManager.getLogger(WhoIs.class);

	// Format string for each users' information
	private static final String format = """
		    User: %s
		      ID: %s
		Nickname: %s
		 Created: %s
		  Joined: %s
		  Avatar: %s
		   Roles: %s
		""";

	public WhoIs(Permission perm) {
		super("whois", perm);
	}

	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getMessage().getChannel();
		List<Member> members = event.getMessage().getMentions().getMembers();
		List<Role> roles = event.getMessage().getMentions().getRoles();

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
		List<MessageCreateData> memberInfo = getInfoAboutMembers(uniqueMembers);
		for (MessageCreateData msg : memberInfo) {
			channel.sendMessage(msg).queue();
		}
	}

	@Override
	public void runSlash(SlashCommandInteractionEvent event) {
		Member member = null;
		for (OptionMapping option : event.getOptionsByName("user")) {
			member = option.getAsMember();
		}

		assert member != null;
		getInfoAboutMembers(List.of(member))
			.forEach(message -> event.reply(message).queue());
	}

	private List<MessageCreateData> getInfoAboutMembers(Collection<Member> members) {
		return members.parallelStream()
			.map(member -> {
				User user = member.getUser();
				return String.format(
					format,
					user.getName(),
					user.getId(),
					member.getEffectiveName(),
					user.getTimeCreated(),
					member.getTimeJoined(),
					user.getEffectiveAvatarUrl(),
					rolesToString(member.getRoles())
				);
			})
			.map(info -> new MessageCreateBuilder()
				.addContent("```yaml\n")
				.addContent(info)
				.addContent("\n```")
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
	public SlashCommandData getCommandData() {
		return super.getDefaultCommandData(getShortDescription())
			.addOption(OptionType.USER, "user", "The user to lookup", true);
	}

}
