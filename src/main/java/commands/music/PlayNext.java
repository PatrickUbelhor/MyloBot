package commands.music;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

/**
 * @author Patrick Ubelhor, Evan Perry Grove
 * @version 12/15/2017, 5/4/2018
 *
 * TODO: Make bot leave voice channel after some period of inactivity
 * TODO: Ability to loop
 * TODO: Create playlist
 */
public final class PlayNext extends Music {

	private boolean active;

	public PlayNext() {
		super("playnext");
		active = false;
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		
		if (args.length < 2 || args.length > 4) return;
		
		// Joins the voice channel if not in one
		if (!active) {
			AudioManager am = event.getGuild().getAudioManager();
			VoiceChannel vc = null;
			
			// Finds the voice channel of the requester
			for (VoiceChannel channel : event.getGuild().getVoiceChannels()) {
				if (channel.getMembers().contains(event.getMember())) {
					vc = channel;
					break;
				}
			}
			
			// Refuses to play if user is not in a voice channel
			if (vc == null) {
				event.getTextChannel().sendMessage("You must be in a voice channel to begin playing music.").queue();
				return;
			}
			
			am.setSendingHandler(new AudioPlayerSendHandler(player));
			am.openAudioConnection(vc);
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
						playerManager.loadItem(song, new MyQueueNextAudioLoadResultHandler(trackScheduler));
					}
					
					break;
				case "song":
					if (!songs.containsKey(args[2])) {
						event.getTextChannel().sendMessage("I can't find that song on my computer, sorry!").queue();
						return;
					}
					
					playerManager.loadItem(songs.get(args[2]), new MyQueueNextAudioLoadResultHandler(trackScheduler));
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
			
			playerManager.loadItem(songs.get(args[1]), new MyQueueNextAudioLoadResultHandler(trackScheduler));
		}
	}
	
	
	@Override
	public String getUsage() {
		return getName() + " <url>";
	}
	
	
	@Override
	public String getDescription() {
		return "Plays the audio from a YouTube video, placing it at the front of the playback queue instead of the rear. given as 'url'";
	}
	
}
