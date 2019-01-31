package commands.admin;

import commands.Command;
import main.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 06/12/2018
 */
public class Mute extends Command {
	
	public Mute(Permission perm) {
		super("mute", perm);
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		TextChannel channel = event.getMessage().getTextChannel();
		List<Member> members = event.getMessage().getMentionedMembers();
		
		// Make sure the user entered at least one @mention
		if (members.isEmpty()) {
			logger.debug("Did not find any @mentions in message");
			channel.sendMessage("You must @mention 1 or more users to mute!").queue();
			return;
		}
		
		// Mute all the members
		for (Member member : members) {
			event.getGuild().getController().setMute(member, true).queue(
					success -> {
						logger.info("Successfully muted " + member.getEffectiveName());
						channel.sendMessage("Successfully muted ").append(member.getEffectiveName()).append("!").queue();
					},
					
					// FIXME: this doesn't actually do any exception handling
					error -> {
						String errorMsg = String.format("Error muting user: %s\n%s", member.getEffectiveName(), error.toString());
						logger.warn(errorMsg);
						channel.sendMessage("Error muting user: ").append(member.getEffectiveName()).queue();
					}
			);
		}
		
	}
	
	
	@Override
	public String getUsage() {
		return "mute @users";
	}
	
	
	@Override
	public String getDescription() {
		return "Server mute the @mentioned users";
	}
}
