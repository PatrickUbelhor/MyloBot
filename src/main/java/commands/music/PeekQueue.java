package commands.music;

import lib.music.Music;
import lib.music.TrackScheduler;
import lib.main.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;


/**
 * @author Patrick Ubelhor
 * @version 5/16/2021
 */
public class PeekQueue extends Music {
	
	public PeekQueue(Permission permission) {
		super("queue", permission);
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		TrackScheduler trackScheduler = Music.trackSchedulers.get(event.getGuild().getIdLong());
		StringBuilder msg = new StringBuilder();
		List<String> titles = trackScheduler.getQueue();
		String currentSong = trackScheduler.getCurrentSong();
		
		if (currentSong == null) {
			event.getTextChannel().sendMessage("No songs in queue!").queue();
			return;
		}
		
		// Show current song
		msg.append("-> ");
		msg.append(currentSong);
		msg.append('\n');
		
		// TODO: add ability to view pages of queue
		// List remaining songs in the queue
		int i = 1;
		for (String title : titles) {
			if (msg.length() + (i + "").length() + ". ".length() + title.length() + 1 >= 2000) {
				event.getTextChannel().sendMessage(msg.toString()).queue();
				msg = new StringBuilder();
			}
			
			msg.append(i);
			msg.append(". ");
			msg.append(title);
			msg.append('\n');
			i++;
		}
		
		event.getTextChannel().sendMessage(msg.toString()).queue();
	}
	
	
	@Override
	public String getUsage() {
		return getName();
	}
	
	
	@Override
	public String getDescription() {
		return "Lists the songs remaining in the queue";
	}
	
}
