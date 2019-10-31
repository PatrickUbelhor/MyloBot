package log;

import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.Date;

/**
 * @author Patrick Ubelhor
 * @version 10/31/2019
 */
class VoiceTrackerEntry {
	
	private String username;
	private Date timeIn;
	private Date timeOut;
	private VoiceChannel channel;
	
	VoiceTrackerEntry() {
		username = null;
		timeIn = null;
		timeOut = null;
		channel = null;
	}
	
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setTimeIn(Date timeIn) {
		this.timeIn = timeIn;
	}
	
	public void setTimeOut(Date timeOut) {
		this.timeOut = timeOut;
	}
	
	public void setChannel(VoiceChannel channel) {
		this.channel = channel;
	}
	
	public String getUsername() {
		return username;
	}
	
	public Date getTimeIn() {
		return timeIn;
	}
	
	public Date getTimeOut() {
		return timeOut;
	}
	
	public VoiceChannel getChannel() {
		return channel;
	}
	
}
