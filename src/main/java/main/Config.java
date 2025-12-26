package main;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lib.exception.LoadConfigException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Patrick Ubelhor
 * @version 12/26/2025
 */
public record Config(
	@JsonProperty("Discord_Token") String DISCORD_TOKEN,
	@JsonProperty("Music_Volume") int MUSIC_VOLUME,
	@JsonProperty("At_Everyone_Path") String AT_EVERYONE_PATH,
	@JsonProperty("Admin_Id") String ADMIN_ID,
	@JsonProperty("Group") Groups groups,
	@JsonProperty("Url") Url url,
	@JsonProperty("Delay") Delay delay,
	@JsonProperty("Interceptors") InterceptorFlag interceptors
) {

	private static final Logger logger = LogManager.getLogger(Config.class);
	private static Config config;

	public record Groups(
		@JsonProperty("Admins") List<String> ADMIN_USER_IDS,
		@JsonProperty("Mods") List<String> MOD_GROUP_IDS
	) {}

	public record Url(
		@JsonProperty("Voice_Tracker") String VOICE_TRACKER
	) {}

	/**
	 * Represent the delay between runs of the service
	 */
	public record Delay(
		@JsonProperty("Ip") long IP
	) {}

	public record InterceptorFlag(
		@JsonProperty("Who_Woulda_Thought") boolean whoWouldaThought,
		@JsonProperty("Twitter_Link_Embed") boolean twitterEmbed,
		@JsonProperty("Mudae_Bot_Rolls") boolean mudaeRolls
	) {}

	public static void load(String filepath)
		throws LoadConfigException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		try {
			config = mapper.readValue(new File(filepath), Config.class);
		} catch (IOException e) {
			logger.fatal("Couldn't read config file", e);
			throw new LoadConfigException(filepath, e);
		}
	}

	public static Config getConfig() {
		return config;
	}

}
