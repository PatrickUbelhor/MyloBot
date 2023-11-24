package main;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lib.exception.LoadConfigException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Patrick Ubelhor
 * @version 11/23/2023
 */
public class Globals {
	
	private static final Logger logger = LogManager.getLogger(Globals.class);
	private static final String CONFIG_PATH = "config/config.yaml";
	
	public static final String DISCORD_TOKEN;
	public static final int MUSIC_VOLUME;
	public static final long SURRENDER_DELAY;
	public static final long IP_CHECK_DELAY;
	public static final List<String> USER_GROUP_IDS; // Group name for basic guild members; TODO: Make empty string allow @everybody
	public static final List<String> MOD_GROUP_IDS;
	public static final String AT_EVERYONE_PATH;
	public static final String VOICE_TRACKER_BASE_URL;
	public static final String SURRENDER_URL;
	public static final boolean ENABLE_WHO_WOULDA_THOUGHT_MEME;
	
	public static final String SERVICES_SAVE_PATH = "config/services.txt";
	
	static {
		// TODO: Generate empty config if no config file exists
		// TODO:
		
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
		SURRENDER_DELAY = config.getSurrenderDelay();
		IP_CHECK_DELAY = config.getIpCheckDelay();
		USER_GROUP_IDS = config.getUserGroupIds();
		MOD_GROUP_IDS = config.getModGroupIds();
		AT_EVERYONE_PATH = config.getAtEveryonePath();
		VOICE_TRACKER_BASE_URL = config.getVoiceTrackerBaseUrl();
		SURRENDER_URL = config.getSurrenderUrl();
		ENABLE_WHO_WOULDA_THOUGHT_MEME = config.getWhoWouldaThoughtisEnabled();
	}
	
}
