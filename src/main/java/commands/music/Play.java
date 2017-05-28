package commands.music;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

/**
 * @author PatrickUbelhor
 * @version 05/27/2017
 */
public final class Play extends Music {
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		
		AudioManager am = event.getGuild().getAudioManager();
		VoiceChannel vc = event.getGuild().getVoiceChannels().get(0);
		
		player.addListener(trackScheduler);
		playerManager.loadItem(args[1], new MyAudioLoadResultHandler(trackScheduler)); // queues the track
		
		am.setSendingHandler(new AudioPlayerSendHandler(player));
		am.openAudioConnection(vc);
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
