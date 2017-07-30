package commands.subscription;

import io.TwitchRequester;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import main.Globals;
import scala.Option;

import static main.Globals.TWITCH_DELAY;
import static main.Globals.logger;

/**
 * @author PatrickUbelhor
 * @version 7/30/2017
 */
public class CheckTwitch extends Service {
	
//	private static final String OUTPUT_FILE_IDS = "./TwitchChannelIds.txt";
	private static final String OUTPUT_FILE_STREAMERS = "./TwitchStreamers.txt";
	private static final LinkedHashMap<String, Boolean> statuses = new LinkedHashMap<>();
	private static final TwitchRequester requester = new TwitchRequester(Globals.TWITCH_CLIENT_ID);
	private static CheckTwitchThread thread = null;
	
	CheckTwitch() {
		super("twitch");
	}
	
	
	@Override
	protected boolean subInit() {
		boolean fileCreated;
		
		// Create file
		try {
			File file = new File(OUTPUT_FILE_STREAMERS);
			fileCreated = file.createNewFile();
			
		} catch (IOException e) {
			logger.error(String.format("Failed to create file: %s", OUTPUT_FILE_STREAMERS));
			return false;
		}
		
		// Read file
		if (!fileCreated) {
			try (BufferedReader br = new BufferedReader(new FileReader(OUTPUT_FILE_STREAMERS))) {
				
				// Load the Twitch IDs of all the subscribed streamers
				String line;
				while ((line = br.readLine()) != null) {
					statuses.put(line, false);
				}
				
			} catch (IOException e) {
				logger.error(String.format("Failed to read file: '%s'", OUTPUT_FILE_STREAMERS), e);
				return false;
			}
		}
		
		return true;
	}
	
	
	@Override
	protected boolean subEnd() {
		// FIXME: save data
		return true;
	}
	
	
	@Override
	protected void startThread() {
		thread = new CheckTwitchThread();
		thread.start();
	}
	
	
	@Override
	protected void endThread() {
		thread.interrupt();
	}
	
	
	private class CheckTwitchThread extends CheckerThread {
		
		
		CheckTwitchThread() {
			super(CheckSurrender.class.getSimpleName(), TWITCH_DELAY);
		}
		
		
		protected void check() {
			
			// If streamer was offline last time and is now online, post message
			for (String streamer : statuses.keySet()) {
				Option<String> link = requester.getStream(streamer);
				
				if (statuses.get(streamer)) {
					if (link.isEmpty()) {
						statuses.put(streamer, false);
					}
					
				} else {
					if (link.nonEmpty()) {
						statuses.put(streamer, true);
						
//						for (MessageChannel m : getActiveChannels()) {
//							m.sendMessage(link.get()).queue();
//						}
						getMediaChannel().sendMessage(link.get()).queue();
					}
				}
			}
		}
	
	}
	
}
