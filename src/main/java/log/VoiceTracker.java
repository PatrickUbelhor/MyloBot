package log;

import main.Globals;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * @author Patrick Ubelhor
 * @version 3/27/2020
 */
public class VoiceTracker implements Closeable {
	
	private final FileWriter fw;
	
	public VoiceTracker() throws IOException {
		 fw = new FileWriter("vclog.csv", true);
	}
	
	
	public void enter(GuildVoiceJoinEvent event) {
		
		Long time = new Date().getTime(); // Get it now before potential lockout
		synchronized (fw) {
			try {
				// Flush to print immediately. If bot goes down, we don't lose data.
				fw.append(String.format("J,%d,%d\n", event.getMember().getIdLong(), time));
				fw.flush();
			} catch (IOException e) {
				Globals.logger.error("Failed to log VC join");
				Globals.logger.error(e);
			}
		}
	}
	
	
	public void exit(GuildVoiceLeaveEvent event) {
		
		Long time = new Date().getTime(); // Get it now before potential lockout
		synchronized (fw) {
			try {
				// Flush to print immediately. If bot goes down, we don't lose data.
				fw.append(String.format("L,%d,%d\n", event.getMember().getIdLong(), time));
				fw.flush();
			} catch (IOException e) {
				Globals.logger.error("Failed to log VC leave");
				Globals.logger.error(e);
			}
		}
	}
	
	
	public void close() throws IOException {
		fw.close();
	}
	
}
