package commands.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author Patrick Ubelhor
 * @version 4/30/2018
 */
public class Pause extends Music {
	
	public Pause() {
		super("pause");
	}
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		trackScheduler.pause();
	}
	
	@Override
	public String getUsage() {
		return getName();
	}
	
	@Override
	public String getDescription() {
		return "Pauses the current track. Can be continued with '!continue'";
	}
	
}
