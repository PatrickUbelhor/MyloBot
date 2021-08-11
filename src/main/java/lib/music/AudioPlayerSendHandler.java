package lib.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;

/**
 * @author Patrick Ubelhor
 * @version 8/20/2019
 */
class AudioPlayerSendHandler implements AudioSendHandler {
	
	private static final Logger logger = LogManager.getLogger(AudioPlayerSendHandler.class);
	
	private final AudioPlayer audioPlayer;
	private AudioFrame lastFrame;
	
	AudioPlayerSendHandler(AudioPlayer audioPlayer) {
		this.audioPlayer = audioPlayer;
	}
	
	@Override
	public boolean canProvide() {
		lastFrame = audioPlayer.provide();
//		logger.debug("[Music] last frame: {}", lastFrame);
		return lastFrame != null;
	}
	
	@Override
	public ByteBuffer provide20MsAudio() {
//		logger.debug("[Music] Providing 20ms frame");
		return ByteBuffer.wrap(lastFrame.getData());
	}
	
	@Override
	public boolean isOpus() {
		return true;
	}
}
