package commands.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author Patrick Ubelhor
 * @version 4/10/2018
 */
public final class Skip extends Music {
	
	public Skip() {
		super("skip");
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		
		// If there's a song playing but nothing in the queue, it will not skip
//		if (trackScheduler.getQueueLength() == 0 && trackScheduler.) {
//			event.getTextChannel().sendMessage("Queue is already empty").queue();
//			return;
//		}
		
		// Clear all songs or a certain number from queue.
		if (args.length > 1) {
			if (args[1].equals("all")) {
				event.getTextChannel().sendMessage("Clearing the queue.").queue();
				
				for (int i = 0; i < trackScheduler.getQueueLength(); i++) {
					trackScheduler.playNext();
				}
				
			} else {
				try {
					int count = Integer.parseInt(args[1]);
					
					if (count < 1) {
						event.getTextChannel().sendMessage("Please give a positive integer.").queue();
					}
					
					while (count > 0) {
						trackScheduler.playNext();
						count--;
					}
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
