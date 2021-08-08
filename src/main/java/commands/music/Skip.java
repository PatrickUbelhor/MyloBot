package commands.music;

import lib.music.Music;
import lib.music.TrackScheduler;
import lib.main.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author Patrick Ubelhor
 * @version 5/16/2021
 */
public final class Skip extends Music {
	
	public Skip(Permission permission) {
		super("skip", permission);
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		TrackScheduler trackScheduler = Music.trackSchedulers.get(event.getGuild().getIdLong());
		
		// Clear all songs or a certain number from queue.
		if (args.length > 1) {
			if (args[1].equals("all")) {
				event.getTextChannel().sendMessage("Clearing the queue.").queue();
				trackScheduler.clearQueue();
				
			} else {
				try {
					int count = Integer.parseInt(args[1]);
					
					if (count < 1) {
						event.getTextChannel().sendMessage("Please give a positive integer.").queue();
						return;
					}
					
					trackScheduler.skip(count);
					
				} catch (NumberFormatException e) {
					event.getTextChannel().sendMessage("Please enter a valid number.").queue();
				}
			}
			
			return;
		}
		
		event.getTextChannel().sendMessage("Skipping...").queue();
		trackScheduler.playNext();
	}
	
	@Override
	public String getUsage() {
		return getName() + " [all|number]";
	}
	
	@Override
	public String getDescription() {
		return "Skips the currently playing song";
	}
	
}
