package commands.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author PatrickUbelhor
 * @version 8/15/2017
 */
public final class Skip extends Music {
	
	public Skip() {
		super("skip");
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		event.getTextChannel().sendMessage("Skipping...").queue();
		trackScheduler.playNext();
	}
	
	@Override
	public String getUsage() {
		return getName();
	}
	
	@Override
	public String getDescription() {
		return "Skips the currently playing song";
	}
	
}
