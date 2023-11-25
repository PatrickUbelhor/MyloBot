package main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lib.exception.LoadConfigException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Patrick Ubelhor
 * @version 11/25/2023
 */
public class Config {

	private static final Logger logger = LogManager.getLogger(Config.class);

	private final String DISCORD_TOKEN;
	private final int MUSIC_VOLUME;
	private final String AT_EVERYONE_PATH;
	private final Group GROUP;
	private final Url URL;
	private final Delay DELAY;
	private final Map<InterceptorFlag, Boolean> INTERCEPTORS;

	record Group(
		@JsonProperty("Users") List<String> USERS,
		@JsonProperty("Mods") List<String> MODS
	) {}

	record Url(
		@JsonProperty("Voice_Tracker") String VOICE_TRACKER
	) {}

	/**
	 * Represent the delay between runs of the service
	 */
	record Delay(
		@JsonProperty("Ip") long IP
	) {}

	enum InterceptorFlag {
		@JsonProperty("Who_Woulda_Thought") whoWouldaThought,
		@JsonProperty("Twitter_Link_Embed") twitterEmbed,
		@JsonProperty("Mudae_Bot_Rolls") mudaeRolls
	}

	public static Config load(String filepath)
		throws LoadConfigException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		try {
			return mapper.readValue(new File(filepath), Config.class);
		} catch (IOException e) {
			logger.fatal("Couldn't read config file", e);
			throw new LoadConfigException(filepath, e);
		}
	}

	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public Config(
		@JsonProperty("Discord_Token") String discordToken,
		@JsonProperty("Music_Volume") int musicVolume,
		@JsonProperty("At_Everyone_Path") String atEveryonePath,
		@JsonProperty("Group") Group group,
		@JsonProperty("Url") Url url,
		@JsonProperty("Delay") Delay delay,
		@JsonProperty("Interceptors") Map<InterceptorFlag, Boolean> interceptors
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

	public Map<InterceptorFlag, Boolean> getInterceptors() {
		return this.INTERCEPTORS;
	}

}
