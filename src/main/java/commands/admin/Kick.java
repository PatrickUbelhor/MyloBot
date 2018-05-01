package commands.admin;

import commands.Command;
import main.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Kick extends Command {

	public Kick(Permission perm) {
		super("kick", perm);
	}


	@Override
	public void run(MessageReceivedEvent event, String[] args) {

	}


	@Override
	public String getUsage() {
		return "kick @user [reason]";
	}


	@Override
	public String getDescription() {
		return "Kicks a user from the guild";
	}
}
