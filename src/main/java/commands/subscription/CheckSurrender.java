package commands.subscription;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import static main.Globals.SURRENDER_DELAY;
import static main.Globals.logger;

/**
 * @author PatrickUbelhor
 * @version 8/16/2017
 */
public class CheckSurrender extends Service {
	
	private static final int NUM_UPDATES = 3;
	private static final String OUTPUT_FILE_LINKS = "./SurrenderUpdates.txt";
	private static final CircularFifoQueue<String> oldLinks = new CircularFifoQueue<>(NUM_UPDATES);
	
	CheckSurrender() {
		super("surrender", SURRENDER_DELAY, OUTPUT_FILE_LINKS);
	}
	
	
	@Override
	protected void parse(String line) {
		oldLinks.add(line);
	}
	
	
	@Override
	protected Collection<String> getLines() {
		return oldLinks;
	}
	
	
	// TODO: Split reading/writing values into separate try blocks
	@Override
	protected List<String> check() {
		
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
		
		return newLinks;
	}
	
}
