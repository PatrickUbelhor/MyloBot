package log;

import main.Globals;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * @author Patrick Ubelhor
 * @version 11/1/2019
 */
public class VoiceTracker {
	
	private final FileWriter fw;
	
	public VoiceTracker() throws IOException {
		 fw = new FileWriter("vclog.csv");
	}
	
	
	public void enter(GuildVoiceJoinEvent event) {
		
		Long time = new Date().getTime(); // Get it now before potential lockout
		synchronized (fw) {
			try {
				fw.append(String.format("J,%d,%d", event.getMember().getIdLong(), time));
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
				fw.append(String.format("L,%d,%d", event.getMember().getIdLong(), time));
			} catch (IOException e) {
				Globals.logger.error("Failed to log VC leave");
				Globals.logger.error(e);
			}
		}
	}
	
}
