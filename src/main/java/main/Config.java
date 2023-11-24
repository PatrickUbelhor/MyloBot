package main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Patrick Ubelhor
 * @version 11/24/2023
 */
public class Config {

	private final String DISCORD_TOKEN;
	private final int MUSIC_VOLUME;
	private final String AT_EVERYONE_PATH;
	private final Group GROUP;
	private final Url URL;
	private final Delay DELAY;
	private final Interceptors INTERCEPTORS;


	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public Config(
		@JsonProperty("Discord_Token") String discordToken,
		@JsonProperty("Music_Volume") int musicVolume,
		@JsonProperty("At_Everyone_Path") String atEveryonePath,
		@JsonProperty("Group") Group group,
		@JsonProperty("Url") Url url,
		@JsonProperty("Delay") Delay delay,
		@JsonProperty("Interceptors") Interceptors interceptors
	) {
		this.DISCORD_TOKEN = discordToken;
		this.MUSIC_VOLUME = musicVolume;
		this.AT_EVERYONE_PATH = atEveryonePath;
		this.GROUP = group;
		this.URL = url;
		this.DELAY = delay;
		this.INTERCEPTORS = interceptors;
	}


	public String getDiscordToken() {
		return DISCORD_TOKEN;
	}


	public int getMusicVolume() {
		return MUSIC_VOLUME;
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


	public Interceptors getInterceptors() {
		return this.INTERCEPTORS;
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

		@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
		Url(
			@JsonProperty("Voice_Tracker") String voiceTracker
		) {
			this.VOICE_TRACKER = voiceTracker;
		}
	}


	/**
	 * Represent the delay between runs of the service
	 */
	static class Delay {
		private final long IP;

		Delay(
			@JsonProperty("Ip") long ip
		) {
			this.IP = ip;
		}
	}


	static class Interceptors {
		public final boolean WHO_WOULDA_THOUGHT;
		public final boolean TWITTER_LINK_EMBED;
		public final boolean MUDAE_BOT_ROLLS;

		Interceptors(
			@JsonProperty("Who_Woulda_Thought") boolean whoWouldaThought,
			@JsonProperty("Twitter_Link_Embed") boolean twitterLinkEmbed,
			@JsonProperty("Mudae_Bot_Rolls") boolean mudaeBotRolls
		) {
			this.WHO_WOULDA_THOUGHT = whoWouldaThought;
			this.TWITTER_LINK_EMBED = twitterLinkEmbed;
			this.MUDAE_BOT_ROLLS = mudaeBotRolls;
		}
	}

}
