package commands.admin;

import commands.Command;
import main.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 2/27/2020
 */
public class Unmute extends Command {
	
	public Unmute(Permission perm) {
		super("unmute", perm);
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		Guild guild = event.getGuild();
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
			guild.mute(member, true).queue(
					success -> {
						logger.info("Successfully unmuted {}", member.getEffectiveName());
						channel.sendMessage("Successfully unmuted ").append(member.getEffectiveName()).append("!").queue();
					},
					
					error -> {
						logger.warn("Error unmuting user: {}\n{}", member.getEffectiveName(), error.toString());
						channel.sendMessage("Error unmuting user: ").append(member.getEffectiveName()).queue();
					}
			);
		}
		
	}
	
	
	@Override
	public String getUsage() {
		return "unmute @users";
	}
	
	
	@Override
	public String getDescription() {
		return "Server unmute the @mentioned users";
	}
}
