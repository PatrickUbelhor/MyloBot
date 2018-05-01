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
 * @version 3/17/2018
 */
public class Globals {
	
	private static final String CONFIG_PATH = "mylobot.properties";
	private static final Properties properties = new Properties();
	
	public static final Logger logger = LogManager.getLogger();
	public static final String DISCORD_TOKEN;
	public static final String TWITCH_CLIENT_ID;
	public static final int MUSIC_VOLUME;
	public static final long SURRENDER_DELAY;
	public static final long TWITCH_DELAY;
	public static final String MEDIA_CHANNEL_ID;
	public static final String USER_GROUP_IDS; // Group name for basic guild members; TODO: Make empty string allow @everybody
	public static final String MOD_GROUP_IDS;
	
	static {
		
		File file = new File(CONFIG_PATH);
		boolean isEmpty = true;
		
		properties.clear();
		
		// Attempts to create config file if it doesn't exist
		try {
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
		DISCORD_TOKEN       = check("discord.token", "");
		TWITCH_CLIENT_ID    = check("twitch.id", "");
		MUSIC_VOLUME        = Integer.parseInt(check("music.volume", "50"));
		SURRENDER_DELAY     = Long.parseLong(check("delay.surrender", "10800000"));
		TWITCH_DELAY        = Long.parseLong(check("delay.twitch", ""));
		MEDIA_CHANNEL_ID    = check("media.channel.id", "");
		USER_GROUP_IDS = check("user.group.name", "");
		MOD_GROUP_IDS = check("mod.group.name", "");
		
		// Put keys in config
		try (FileWriter fw = new FileWriter(file)) {
			properties.store(fw, "Properties for MyloBot");
		} catch (IOException e) {
			logger.error(String.format("Could not write to '%s'.", CONFIG_PATH), e);
		}
		
	}
	
	
	private static String check(String key, String defaultValue) {
		if (!properties.containsKey(key)) {
			logger.error(String.format("Config '%s' does not contain key '%s'. Using default value: '%s'", CONFIG_PATH, key, defaultValue));
			properties.setProperty(key, defaultValue);
		}
		
		return properties.getProperty(key);
	}
	
}
