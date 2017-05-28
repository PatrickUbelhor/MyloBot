package commands.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author PatrickUbelhor
 * @version 05/27/2017
 */
public final class Skip extends Music {
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		trackScheduler.playNext();
	}
	
	
	@Override
	public String getUsage() {
		return "skip";
	}
	
	
	@Override
	public String getDescription() {
		return "Skips the currently playing song";
	}
	
}
