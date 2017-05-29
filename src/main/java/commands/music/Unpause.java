package commands.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author PatrickUbelhor
 * @version 05/28/2017
 */
public class Unpause extends Music {
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		trackScheduler.unpause();
	}
	
	@Override
	public String getUsage() {
		return "continue";
	}
	
	@Override
	public String getDescription() {
		return "Continues playing a paused track";
	}
}
