package clients;

import main.Globals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 3/10/2021
 */
public class SurrenderClient {
	
	public SurrenderClient() {}
	
	
	public List<String> getLinks(int numLinks) {
		
		URL url;
		BufferedReader br = null;
		LinkedList<String> newLinks = new LinkedList<>();
		
		
		try {
			url = new URL(Globals.SURRENDER_URL);
			br = new BufferedReader(new InputStreamReader(url.openStream()));
			
			int i = 0;
			String line;
			boolean keyFound = false;
			while (i < numLinks && (line = br.readLine()) != null) {
				
				if (line.contains("blog-posts hfeed")) {
					keyFound = true;
				} else if (line.contains("blog-pager")) {
					keyFound = false;
				}
				
				if (keyFound && line.contains("news-title")) {
					String anchor = br.readLine();
					logger.debug("[S@20] Found anchor: {}", anchor);
					newLinks.addFirst(anchor.split("'")[1]);
					i++;
				}
			}
			
		} catch (IOException e) {
			logger.error("[S@20] Failed to retrieve S@20 links", e);
			
		} finally {
			
			// Close out buffered reader
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error("[S@20] Failed to properly close S220 BufferedReader", e);
				}
			}
		}
		
		for (String link : newLinks) {
			logger.debug("[S@20] Found link: '{}'", link);
		}
		
		return newLinks;
	}
	
}
