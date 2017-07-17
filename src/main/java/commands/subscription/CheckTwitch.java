package commands.subscription;

import io.TwitchRequester;
import main.Globals;
import net.dv8tion.jda.core.entities.MessageChannel;
import scala.Option;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;

import static main.Globals.TWITCH_DELAY;

/**
 * @author PatrickUbelhor
 * @version 7/1/2017
 */
public class CheckTwitch extends Service {
	
	private static final String OUTPUT_FILE_IDS = "./TwitchChannelIds.txt";
	private static final String OUTPUT_FILE_STREAMERS = "./TwitchStreamers.txt";
	private static final LinkedHashMap<String, Boolean> statuses = new LinkedHashMap<>();
	private static final TwitchRequester requester = new TwitchRequester(Globals.TWITCH_CLIENT_ID);
	
	CheckTwitch() {
		super(OUTPUT_FILE_IDS);
	}
	
	protected CheckerThread getNewCheckerThread() {
		return new CheckTwitchThread();
	}
	
	protected String getName() {
		return "twitch";
	}
	
	protected boolean loadData() {
		BufferedReader br = null;
		String line;
		
		try {
			
			// Load the Twitch IDs of all the subscribed streamers
			File streamers = new File(OUTPUT_FILE_STREAMERS);
			if (!streamers.createNewFile()) {
				
				br = new BufferedReader(new FileReader(streamers));
				while ((line = br.readLine()) != null) {
					statuses.put(line, false);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
			
		} finally {
			
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return true;
	}
	
	
	@Override
	protected boolean containsSource(String source) {
		return statuses.containsKey(source);
	}
	
	
	@Override
	protected boolean addSource(String source) {
		// FIXME: actually validate without exception handling
		statuses.put(requester.getUserId(source), false);
		return true;
	}
	
	
	@Override
	protected boolean removeSource(String source) {
		statuses.remove(source);
		return statuses.isEmpty();
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
						
						for (MessageChannel m : getActiveChannels()) {
							m.sendMessage(link.get());
						}
					}
				}
			}
		}
	
	}
	
}
