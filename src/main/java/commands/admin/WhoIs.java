package commands.admin;

import commands.Command;
import main.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class WhoIs extends Command {

	public WhoIs(Permission perm) {
		super("who", perm);
	}


	@Override
	public void run(MessageReceivedEvent event, String[] args) {

	}

	@Override
	public String getUsage() {
		return "who @user";
	}

	@Override
	public String getDescription() {
		return "Gives information about the target user";
	}
}
