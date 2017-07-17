package commands.subscription;

import net.dv8tion.jda.core.entities.MessageChannel;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.io.*;
import java.net.URL;
import java.util.LinkedList;

import static main.Globals.SURRENDER_DELAY;
import static main.Globals.logger;

/**
 * @author PatrickUbelhor
 * @version 7/1/2017
 */
public class CheckSurrender extends Service {
	
	private static final int NUM_UPDATES = 3;
	private static final String OUTPUT_FILE_LINKS = "./SurrenderUpdates.txt";
	private static final String OUTPUT_FILE_IDS = "./SurrenderChannelIDs.txt";
	private static final CircularFifoQueue<String> oldLinks = new CircularFifoQueue<>(NUM_UPDATES);
	
	CheckSurrender() {
		super(OUTPUT_FILE_IDS);
	}
	
	protected CheckerThread getNewCheckerThread() {
		return new CheckSurrenderThread();
	}
	
	
	protected String getName() {
		return "surrender";
	}
	
	
	protected boolean loadData() {
		BufferedReader br = null;
		String line;

		// TODO: Split file creation and reading into separate try blocks
		try {

			// Try to load all of the news links
			File links = new File(OUTPUT_FILE_LINKS);
			if (!links.createNewFile()) {

				br = new BufferedReader(new FileReader(OUTPUT_FILE_LINKS));
				while ((line = br.readLine()) != null) {
					oldLinks.add(line);
				}
			}

		} catch (IOException e) {

			logger.error(String.format("Failed to access '%s'", OUTPUT_FILE_LINKS), e);
			return false;

		} finally {

			// Closes the BufferedReader
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error("Failed to close S@20 init BufferedReader.", e);
				}
			}
		}

		return true;
	}
	
	
	private class CheckSurrenderThread extends CheckerThread {
		
		CheckSurrenderThread() {
			super(CheckSurrender.class.getSimpleName(), SURRENDER_DELAY);
		}
		
		
		// TODO: Split reading/writing values into separate try blocks
		protected void check() {
			
			URL url;
			BufferedReader br = null;
			FileWriter fw = null;
			LinkedList<String> newLinks = new LinkedList<>();
			boolean keyFound = false;
			
			
			try {
				url = new URL("http://www.surrenderat20.net/search/label/Releases/");
				br = new BufferedReader(new InputStreamReader(url.openStream()));
				fw = new FileWriter(OUTPUT_FILE_LINKS, true);
				
				
				// Fetches the links from S@20 webpage
				int i = 0;
				String line;
				while (i < NUM_UPDATES && (line = br.readLine()) != null) {
					
					if (line.contains("blog-posts hfeed")) {
						keyFound = true;
					} else if (line.contains("blog-pager")) {
						keyFound = false;
					}
					
					if (keyFound && line.contains("news-title")) {
						newLinks.addFirst(br.readLine().split("\'")[1]);
						i++;
					}
				}
				
				
				// Removes old links and writes new ones to file
				newLinks.removeAll(oldLinks);
				for (String link : newLinks) {
					fw.append(link);
					fw.append("\n");
				}
				
				
				// Updates the old links
				oldLinks.addAll(newLinks);
				
			} catch (IOException e) {
				logger.error("Failed to check S@20.", e);
			} finally {
				
				// Close out buffered reader
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						logger.error("Failed to close S@20 BufferedReader.", e);
					}
				}
				
				// Close out file writer
				if (fw != null) {
					try {
						fw.close();
					} catch (IOException e) {
						logger.error("Failed to close S@20 FileWriter.", e);
					}
				}
			}
			
			// Sends the new links to the subscribed channels
			for (String result : newLinks) {
				if (result != null) {
					for (MessageChannel c : getActiveChannels()) {
						c.sendMessage(result).queue();
					}
				}
			}
			
		}
		
	}
	
}
