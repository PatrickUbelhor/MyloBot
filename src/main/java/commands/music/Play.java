package commands.music;

import lib.music.Music;
import lib.music.QueueLastAudioLoadResultHandler;
import lib.music.TrackScheduler;
import lib.main.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author Patrick Ubelhor
 * @version 5/16/2021
 *
 * TODO: Make bot leave voice channel after some period of inactivity
 * TODO: Ability to loop
 */
public final class Play extends Music {
	
	
	public Play(Permission permission) {
		super("play", permission);
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		
		if (args.length < 2) return;
		
		// Joins the voice channel if not in one
		if (!joinAudioChannel(event)) {
			return; // If we failed to join a voice channel, return
		}
		
		// Directly add song to queue and return if it's a link
		if (args[1].startsWith("http") || args[1].startsWith("www")) {
			TrackScheduler trackScheduler = Music.trackSchedulers.get(event.getGuild().getIdLong());
			playerManager.loadItem(args[1], new QueueLastAudioLoadResultHandler(trackScheduler));
			return;
		}
		
		// Build YouTube query
		StringBuilder queryBuilder = new StringBuilder();
		for (int i = 1; i < args.length; i++) {
			queryBuilder.append(args[i]);
			queryBuilder.append(' ');
		}
		String query = queryBuilder.toString().strip();
		
		// TODO: send YouTube query
		// TODO: Fix PlayNext
				
	}
	
	
	@Override
	public String getUsage() {
		return getName() + " <url>";
	}
	
	
	@Override
	public String getDescription() {
		return "Plays the audio from a YouTube video, given as 'url'";
	}
	
}
