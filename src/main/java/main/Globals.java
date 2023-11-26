package main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * @author Patrick Ubelhor
 * @version 11/25/2023
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

		Config config = Config.load(CONFIG_PATH);

		DISCORD_TOKEN = config.discordToken();
		MUSIC_VOLUME = config.musicVolume();
		IP_CHECK_DELAY = config.delay().IP();
		USER_GROUP_IDS = config.group().USERS();
		MOD_GROUP_IDS = config.group().MODS();
		AT_EVERYONE_PATH = config.atEveryonePath();
		VOICE_TRACKER_BASE_URL = config.url().VOICE_TRACKER();
		WHO_WOULDA_THOUGHT_ENABLED = config.interceptors().getOrDefault(Config.InterceptorFlag.whoWouldaThought, true);
		TWITTER_LINK_EMBED_ENABLED = config.interceptors().getOrDefault(Config.InterceptorFlag.twitterEmbed, true);
		MUDAE_BOT_ROLLS_ENABLED = config.interceptors().getOrDefault(Config.InterceptorFlag.mudaeRolls, true);
	}

}
