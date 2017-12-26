package commands.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author Patrick Ubelhor
 * @version 8/15/2017
 */
public class Unpause extends Music {
	
	public Unpause() {
		super("unpause");
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		trackScheduler.unpause();
	}
	
	@Override
	public String getUsage() {
		return getName();
	}
	
	@Override
	public String getDescription() {
		return "Continues playing a paused track";
	}
	
}
