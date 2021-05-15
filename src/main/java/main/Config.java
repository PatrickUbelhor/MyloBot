package main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Patrick Ubelhor
 * @version 5/15/2021
 */
public class Config {
	
	private final String DISCORD_TOKEN;
	private final int MUSIC_VOLUME;
	private final String AT_EVERYONE_PATH;
	private final Group GROUP;
	private final Url URL;
	private final Delay DELAY;
	private final boolean WHO_WOULDA_THOUGHT_ENABLED;
	
	
	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public Config(
			@JsonProperty("Discord_Token") String discordToken,
			@JsonProperty("Music_Volume") int musicVolume,
			@JsonProperty("At_Everyone_Path") String atEveryonePath,
			@JsonProperty("Group") Group group,
			@JsonProperty("Url") Url url,
			@JsonProperty("Delay") Delay delay,
			@JsonProperty("Who_Woulda_Thought_Enabled") boolean whoWouldaThoughtIsEnabled
	) {
		this.DISCORD_TOKEN = discordToken;
		this.MUSIC_VOLUME = musicVolume;
		this.AT_EVERYONE_PATH = atEveryonePath;
		this.GROUP = group;
		this.URL = url;
		this.DELAY = delay;
		this.WHO_WOULDA_THOUGHT_ENABLED = whoWouldaThoughtIsEnabled;
	}
	
	
	public String getDiscordToken() {
		return DISCORD_TOKEN;
	}
	
	
	public int getMusicVolume() {
		return MUSIC_VOLUME;
	}
	
	
	public long getSurrenderDelay() {
		return DELAY.SURRENDER;
	}
	
	
	public long getIpCheckDelay() {
		return DELAY.IP;
	}
	
	
	public List<String> getUserGroupIds() {
		return GROUP.USERS;
	}
	
	
	public List<String> getModGroupIds() {
		return GROUP.MODS;
	}
	
	
	public String getAtEveryonePath() {
		return AT_EVERYONE_PATH;
	}
	
	
	public String getVoiceTrackerBaseUrl() {
		return URL.VOICE_TRACKER;
	}
	
	
	public String getSurrenderUrl() {
		return URL.SURRENDER;
	}
	
	
	public boolean getWhoWouldaThoughtisEnabled() {
		return WHO_WOULDA_THOUGHT_ENABLED;
	}
	
	
	static class Group {
		private final List<String> USERS;
		private final List<String> MODS;
		
		Group(
				@JsonProperty("Users") List<String> users,
				@JsonProperty("Mods") List<String> mods
		) {
			this.USERS = users;
			this.MODS = mods;
		}
	}
	
	
	static class Url {
		private final String VOICE_TRACKER;
		private final String SURRENDER;
		
		@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
		Url(
				@JsonProperty("Voice_Tracker") String voiceTracker,
				@JsonProperty("Surrender") String surrender
		) {
			this.VOICE_TRACKER = voiceTracker;
			this.SURRENDER = surrender;
		}
	}
	
	
	static class Delay {
		private final long SURRENDER;
		private final long IP;
		
		Delay(
				@JsonProperty("Surrender") long surrender,
				@JsonProperty("Ip") long ip
		) {
			this.SURRENDER = surrender;
			this.IP = ip;
		}
	}
	
}
