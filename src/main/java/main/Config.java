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
//@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
public record Config(
	@JsonProperty("Discord_Token") String discordToken,
	@JsonProperty("Music_Volume") int musicVolume,
	@JsonProperty("At_Everyone_Path") String atEveryonePath,
	@JsonProperty("Group") Group group,
	@JsonProperty("Url") Url url,
	@JsonProperty("Delay") Delay delay,
	@JsonProperty("Interceptors") Map<InterceptorFlag, Boolean> interceptors
) {

	private static final Logger logger = LogManager.getLogger(Config.class);

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

}
