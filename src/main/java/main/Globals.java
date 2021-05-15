package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lib.exception.LoadConfigException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Patrick Ubelhor
 * @version 5/15/2021
 */
public class Globals {
	
	public static final Logger logger = LogManager.getLogger(Globals.class);
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
	
	private static final String CONFIG_PATH = "config/mylobot.properties";
	private static final Properties properties = new Properties();
	private static boolean gotAllRequiredProperties = true;
	
	static {
		
		Config config;
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		try {
			config = mapper.readValue(new File("config/config.yaml"), Config.class);
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
		
//		File file = new File(CONFIG_PATH);
//		boolean isEmpty = true;
//
//		properties.clear();
//
//		// Attempts to create config file if it doesn't exist
//		try {
//			file.getParentFile().mkdirs();
//			isEmpty = file.createNewFile();
//		} catch (IOException e) {
//			logger.error(String.format("Could not create '%s' file.", CONFIG_PATH), e);
//		}
//
//		// Grabs values from config file
//		if (!isEmpty) {
//			try (FileInputStream fis = new FileInputStream(file)) {
//				properties.load(fis);
//			} catch (IOException e) {
//				logger.error(String.format("Could not read from '%s'.", CONFIG_PATH), e);
//			}
//		}
//
//		// Initializes constants
//		DISCORD_TOKEN       = getOrFail("discord.token");
//		USER_GROUP_IDS      = getOrFail("user.group.ids");
//		MOD_GROUP_IDS       = getOrFail("mod.group.ids");
//		VOICE_TRACKER_BASE_URL  = getOrFail("url.voicetracker");
//		SURRENDER_URL       = getOrFail("url.surrender");
//		MUSIC_VOLUME        = Integer.parseInt(getOrDefault("music.volume", "50"));
//		SURRENDER_DELAY     = Long.parseLong(getOrDefault("delay.surrender", "10800000"));
//		IP_CHECK_DELAY      = Long.parseLong(getOrDefault("delay.ip", "3600000"));
//		AT_EVERYONE_PATH    = getOrDefault("path.at.everyone", "config/AtEveryone");
//		ENABLE_WHO_WOULDA_THOUGHT_MEME  = Boolean.parseBoolean(getOrDefault("intercept.who_woulda_thought", "true"));
//
//		// Put keys in config
//		try (FileWriter fw = new FileWriter(file)) {
//			properties.store(fw, "Properties for MyloBot");
//		} catch (IOException e) {
//			logger.error(String.format("Could not write to '%s'.", CONFIG_PATH), e);
//		}
//
//		if (!gotAllRequiredProperties) {
//			throw new RuntimeException("Failed to fetch all required configuration properties");
//		}
	}
	
	
	private static String getOrDefault(String key, String defaultValue) {
		if (!properties.containsKey(key) || properties.getProperty(key).equals("")) {
			logger.warn(String.format("Config '%s' does not contain key '%s'. Using default value: '%s'", CONFIG_PATH, key, defaultValue));
			properties.setProperty(key, defaultValue);
		}
		
		return properties.getProperty(key);
	}
	
	private static String getOrFail(String key) {
		if (!properties.containsKey(key) || properties.getProperty(key).equals("")) {
			logger.error(String.format("Config '%s' does not contain value for required key '%s'", CONFIG_PATH, key));
			gotAllRequiredProperties = false;
			properties.setProperty(key, ""); // Ensures empty value gets printed to file
			return null;
		}
		
		return properties.getProperty(key);
	}
	
}
