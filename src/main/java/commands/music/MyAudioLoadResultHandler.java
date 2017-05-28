package commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

/**
 * @author PatrickUbelhor
 * @version 05/27/2017
 */
class MyAudioLoadResultHandler implements AudioLoadResultHandler {
	private final TrackScheduler trackScheduler;
	
	MyAudioLoadResultHandler(TrackScheduler trackScheduler) {
		this.trackScheduler = trackScheduler;
	}
	
	@Override
	public void trackLoaded(AudioTrack track) {
		System.out.println("Loaded track");
		trackScheduler.queue(track);
	}
	
	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		System.out.println("Loaded playlist");
		for (AudioTrack track : playlist.getTracks()) {
			trackScheduler.queue(track);
		}
	}
	
	@Override
	public void noMatches() {
		// Notify the user that we've got nothing
		System.out.println("No matches!");
	}
	
	@Override
	public void loadFailed(FriendlyException throwable) {
		// Notify the user that everything exploded
		System.out.println("Load failed!");
		System.out.println(throwable.severity.name());
		System.out.println(throwable.getMessage());
//		throwable.printStackTrace();
	}
}
