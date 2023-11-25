package main;

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
 * @version 11/24/2023
 */
public class Globals {

	private static final Logger logger = LogManager.getLogger(Globals.class);
	private static final String CONFIG_PATH = "config/config.yaml";

	public static final String DISCORD_TOKEN;
	public static final int MUSIC_VOLUME;
	public static final long IP_CHECK_DELAY;
	public static final List<String> USER_GROUP_IDS; // Group name for basic guild members; TODO: Make empty string allow @everybody
	public static final List<String> MOD_GROUP_IDS;
	public static final String AT_EVERYONE_PATH;
	public static final String VOICE_TRACKER_BASE_URL;
	public static final boolean WHO_WOULDA_THOUGHT_ENABLED;
	public static final boolean TWITTER_LINK_EMBED_ENABLED;
	public static final boolean MUDAE_BOT_ROLLS_ENABLED;

	public static final String SERVICES_SAVE_PATH = "config/services.txt";

	static {
		// TODO: Generate empty config if no config file exists

		Config config;
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		try {
			config = mapper.readValue(new File(CONFIG_PATH), Config.class);
		} catch (IOException e) {
			logger.fatal("Couldn't read config file", e);
			throw new LoadConfigException(e);
		}

		DISCORD_TOKEN = config.getDiscordToken();
		MUSIC_VOLUME = config.getMusicVolume();
		IP_CHECK_DELAY = config.getIpCheckDelay();
		USER_GROUP_IDS = config.getUserGroupIds();
		MOD_GROUP_IDS = config.getModGroupIds();
		AT_EVERYONE_PATH = config.getAtEveryonePath();
		VOICE_TRACKER_BASE_URL = config.getVoiceTrackerBaseUrl();
		WHO_WOULDA_THOUGHT_ENABLED = config.getInterceptors().getOrDefault("Who_Woulda_Thought", true);
		TWITTER_LINK_EMBED_ENABLED = config.getInterceptors().getOrDefault("Twitter_Link_Embed", true);
		MUDAE_BOT_ROLLS_ENABLED = config.getInterceptors().getOrDefault("Mudae_Bot_Rolls", true);
	}

}
