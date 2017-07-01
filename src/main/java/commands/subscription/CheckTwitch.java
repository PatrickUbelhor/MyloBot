package commands.subscription;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * @author PatrickUbelhor
 * @version 06/30/2017
 */
public class CheckTwitch extends Service {
	
	private static final long DELAY_TIME = 1000 * 60 * 5; // 5 minutes
	private static final String OUTPUT_FILE_IDS = "./TwitchChannelIds.txt";
	private static final String OUTPUT_FILE_STREAMERS = "./TwitchStreamers.txt";
	private static final LinkedHashMap<String, Boolean> statuses = new LinkedHashMap<>();
	
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
	
	
	private class CheckTwitchThread extends CheckerThread {
		
		CheckTwitchThread() {
			super(CheckSurrender.class.getSimpleName(), DELAY_TIME);
		}
		
		
		protected void check() {
		
		}
		
	
	}
	
}
