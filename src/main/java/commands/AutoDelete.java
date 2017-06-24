package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author PatrickUbelhor
 * @version 06/18/2017
 */
public class AutoDelete extends Command {
	
	
	public void run(MessageReceivedEvent event, String[] args) {
	
	}
	
	
	public String getUsage() {
		return "autodelete [save]";
	}
	
	
	public String getDescription() {
		return "Automatically deletes messages from this channel. Can optionally save embedded content.";
	}
	
}
