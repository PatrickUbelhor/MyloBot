package log;

import main.Globals;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * @author Patrick Ubelhor
 * @version 2/12/2021
 */
public class VoiceTracker implements Closeable {
	
	private final FileWriter fw;
	
	public VoiceTracker() throws IOException {
		 fw = new FileWriter("vclog.csv", true);
	}
	
	
	public void enter(GuildVoiceJoinEvent event) {
		this.logEvent("J", event.getMember().getIdLong(), event.getChannelJoined().getIdLong());
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
				Globals.logger.error("Failed to log VC {} event", "M");
				Globals.logger.error(e);
			}
		}
	}
	
	
	public void exit(GuildVoiceLeaveEvent event) {
		this.logEvent("L", event.getMember().getIdLong(), event.getChannelLeft().getIdLong());
	}
	
	
	private void logEvent(String eventCode, Long userId, Long channelId) {
		
		Long time = new Date().getTime(); // Get it now before potential lockout
		synchronized (fw) {
			try {
				// Flush to print immediately. If bot goes down, we don't lose data.
				fw.append(String.format("%s,%d,%d,%d\n", eventCode, userId, time, channelId));
				fw.flush();
			} catch (IOException e) {
				Globals.logger.error("Failed to log VC {} event", eventCode);
				Globals.logger.error(e);
			}
		}
	}
	
	
	public void close() throws IOException {
		fw.close();
	}
	
}
