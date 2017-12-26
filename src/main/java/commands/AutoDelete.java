package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author Patrick Ubelhor
 * @version 8/15/2017
 */
public class AutoDelete extends Command {
	
	public AutoDelete() {
		super("autodelete");
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		// TODO: fill
	}
	
	
	@Override
	public String getUsage() {
		return getName() + " [save]";
	}
	
	
	@Override
	public String getDescription() {
		return "Automatically deletes messages from this channel. Can optionally save embedded content.";
	}
	
}
