package commands.music;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

import java.io.File;

/**
 * @author PatrickUbelhor
 * @version 05/29/2017
 *
 * TODO: Make bot leave voice channel after some period of inactivity
 */
public final class Play extends Music {
	
	private boolean active;
	
	public Play() {
		super();
		active = false;
	}
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		
		if (args.length != 2) return;
		
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
				event.getTextChannel().sendMessage("Must be in a voice channel to begin playing music.").queue();
				return;
			}
			
			am.setSendingHandler(new AudioPlayerSendHandler(player));
			am.openAudioConnection(vc);
		}
		
		
		playerManager.loadItem(args[1], new MyAudioLoadResultHandler(trackScheduler)); // queues the track
		
	}
	
	
	@Override
	public String getUsage() {
		return "play <url>";
	}
	
	
	@Override
	public String getDescription() {
		return "Plays the audio from a YouTube video, given as 'url'";
	}
	
}
