package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Patrick Ubelhor
 * @version 9/19/2019
 * TODO: Make custom exception types
 */
public class Globals {
	
	public static final Logger logger = LogManager.getLogger(Globals.class);
	public static final String DISCORD_TOKEN;
	public static final int MUSIC_VOLUME;
	public static final long SURRENDER_DELAY;
	public static final long IP_CHECK_DELAY;
	public static final String USER_GROUP_IDS; // Group name for basic guild members; TODO: Make empty string allow @everybody
	public static final String MOD_GROUP_IDS;
	public static final String AT_EVERYONE_PATH;
	public static final String VOICE_TRACKER_BASE_URL;
	public static final boolean ENABLE_WHO_WOULDA_THOUGHT_MEME;
	
	private static final String CONFIG_PATH = "config/mylobot.properties";
	private static final Properties properties = new Properties();
	private static boolean gotAllRequiredProperties = true;
	
	static {
		
		File file = new File(CONFIG_PATH);
		boolean isEmpty = true;
		
		properties.clear();
		
		// Attempts to create config file if it doesn't exist
		try {
			file.getParentFile().mkdirs();
			isEmpty = file.createNewFile();
		} catch (IOException e) {
			logger.error(String.format("Could not create '%s' file.", CONFIG_PATH), e);
		}
		
		// Grabs values from config file
		if (!isEmpty) {
			try (FileInputStream fis = new FileInputStream(file)) {
				properties.load(fis);
			} catch (IOException e) {
				logger.error(String.format("Could not read from '%s'.", CONFIG_PATH), e);
			}
		}
		
		// Initializes constants
		DISCORD_TOKEN       = getOrFail("discord.token");
		USER_GROUP_IDS      = getOrFail("user.group.ids");
		MOD_GROUP_IDS       = getOrFail("mod.group.ids");
		VOICE_TRACKER_BASE_URL  = getOrFail("url.voicetracker");
		MUSIC_VOLUME        = Integer.parseInt(getOrDefault("music.volume", "50"));
		SURRENDER_DELAY     = Long.parseLong(getOrDefault("delay.surrender", "10800000"));
		IP_CHECK_DELAY      = Long.parseLong(getOrDefault("delay.ip", "3600000"));
		AT_EVERYONE_PATH    = getOrDefault("path.at.everyone", "config/AtEveryone");
		ENABLE_WHO_WOULDA_THOUGHT_MEME  = Boolean.parseBoolean(getOrDefault("intercept.who_woulda_thought", "true"));
		
		// Put keys in config
		try (FileWriter fw = new FileWriter(file)) {
			properties.store(fw, "Properties for MyloBot");
		} catch (IOException e) {
			logger.error(String.format("Could not write to '%s'.", CONFIG_PATH), e);
		}
		
		if (!gotAllRequiredProperties) {
			throw new RuntimeException("Failed to fetch all required configuration properties");
		}
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
