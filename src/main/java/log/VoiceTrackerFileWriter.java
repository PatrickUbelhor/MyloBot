package log;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * @author Patrick Ubelhor
 * @version 12/31/2021
 */
public class VoiceTrackerFileWriter implements Closeable {
	
	private final Logger logger = LogManager.getLogger(VoiceTrackerFileWriter.class);
	
	private final FileWriter fw;
	
	public VoiceTrackerFileWriter() throws IOException {
		 fw = new FileWriter("vclog.csv", true);
	}
	
	
	public void enter(VoiceChannel channel, Member member) {
		this.logEvent("J", member.getIdLong(), channel.getIdLong());
	}
	
	
	public void move(GuildVoiceMoveEvent event) {
		Long userSnowflake = event.getMember().getIdLong();
		Long time = new Date().getTime(); // Get it now before potential lockout
		Long leavingChannelId = event.getChannelLeft().getIdLong();
		Long joiningChannelId = event.getChannelJoined().getIdLong();
		
		synchronized (fw) {
			try {
				// Flush to print immediately. If bot goes down, we don't lose data.
				fw.append(String.format("M,%d,%d,%d,%d\n", userSnowflake, time, leavingChannelId, joiningChannelId));
				fw.flush();
			} catch (IOException e) {
				logger.error("Failed to log VC {} event", "M", e);
			}
		}
	}
	
	
	public void exit(VoiceChannel channel, Member member) {
		this.logEvent("L", member.getIdLong(), channel.getIdLong());
	}
	
	
	private void logEvent(String eventCode, Long userId, Long channelId) {
		
		Long time = new Date().getTime(); // Get it now before potential lockout
		synchronized (fw) {
			try {
				// Flush to print immediately. If bot goes down, we don't lose data.
				fw.append(String.format("%s,%d,%d,%d\n", eventCode, userId, time, channelId));
				fw.flush();
			} catch (IOException e) {
				logger.error("Failed to log VC {} event", eventCode, e);
			}
		}
	}
	
	
	public void close() throws IOException {
		synchronized (fw) {
			try {
				fw.close();
			} catch (IOException e) {
				logger.error("Failed to close FileWriter for VoiceTracker", e);
			}
		}
	}
	
}
