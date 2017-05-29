package commands.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author PatrickUbelhor
 * @version 05/28/2017
 *
 * TODO: Disallow if no track is currently playing. Do this in TrackScheduler
 */
public class Pause extends Music {
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		trackScheduler.pause();
	}
	
	@Override
	public String getUsage() {
		return "pause";
	}
	
	@Override
	public String getDescription() {
		return "Pauses the current track. Can be continued with '!continue'";
	}
}
