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
 * @version 06/09/2018
 */
public class Unmute extends Command {
	
	public Unmute(Permission perm) {
		super("unmute", perm);
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		TextChannel channel = event.getMessage().getTextChannel();
		List<Member> members = event.getMessage().getMentionedMembers();
		
		// Make sure the user entered at least one @mention
		if (members.isEmpty()) {
			logger.debug("Did not find any @mentions in message");
			channel.sendMessage("You must @mention 1 or more users to unmute!").queue();
			return;
		}
		
		// Mute all the members
		for (Member member : members) {
			event.getGuild().getController().setMute(member, true).queue(
					success -> {
						logger.info("Successfully unmuted " + member.getEffectiveName());
						channel.sendMessage("Successfully unmuted ").append(member.getEffectiveName()).append("!").queue();
					},
					
					error -> {
						String errorMsg = String.format("Error unmuting user: %s\n%s", member.getEffectiveName(), error.toString());
						logger.warn(errorMsg);
						channel.sendMessage("Error unmuting user: ").append(member.getEffectiveName()).queue();
					}
			);
		}
		
	}
	
	
	@Override
	public String getUsage() {
		return "unmute @user1 @user2 ...";
	}
	
	
	@Override
	public String getDescription() {
		return "Server unmute the @mentioned users";
	}
}
