package commands.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * @author Patrick Ubelhor
 * @version 5/10/2018
 *
 * TODO: Make bot leave voice channel after some period of inactivity
 * TODO: Ability to loop
 * TODO: Create playlist
 */
public final class Play extends Music {
	
	private boolean active;
	
	public Play() {
		super("play");
		active = false;
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		
		if (args.length < 2 || args.length > 4) return;
		
		// Joins the voice channel if not in one
		if (!active) {
			if (!joinAudioChannel(event)) {
				return; // If we failed to join a voice channel, return
			}
		}
		
		if (args.length == 3) {
			switch (args[1]) {
				case "album":
					if (!albums.containsKey(args[2])) {
						event.getTextChannel().sendMessage("I can't find that album on my computer, sorry!").queue();
						return;
					}
					
					for (String song : albums.get(args[2])) {
						System.out.println(song);
						playerManager.loadItem(song, new MyAudioLoadResultHandler(trackScheduler));
					}
					
					break;
				case "song":
					if (!songs.containsKey(args[2])) {
						event.getTextChannel().sendMessage("I can't find that song on my computer, sorry!").queue();
						return;
					}
					
					playerManager.loadItem(songs.get(args[2]), new MyAudioLoadResultHandler(trackScheduler));
					break;
				default:
					event.getTextChannel().sendMessage("Unknown argument for 'play'").queue();
					return;
			}
			
			return;
		}
		
		
		if (args[1].startsWith("http") || args[1].startsWith("www")) {
			playerManager.loadItem(args[1], new MyAudioLoadResultHandler(trackScheduler));
		} else {
			if (!songs.containsKey(args[1])) {
				event.getTextChannel().sendMessage("I can't find that song on my computer, sorry!").queue();
				return;
			}
			
			playerManager.loadItem(songs.get(args[1]), new MyAudioLoadResultHandler(trackScheduler));
		}
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
