package clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import static main.Globals.logger;

public class SurrenderClient {
	
	public SurrenderClient() {}
	
	
	public List<String> getLinks(int numLinks) {
		
		URL url;
		BufferedReader br = null;
		LinkedList<String> newLinks = new LinkedList<>();
		boolean keyFound = false;
		
		
		try {
			url = new URL("http://www.surrenderat20.net/search/label/Releases");
			br = new BufferedReader(new InputStreamReader(url.openStream()));
			
			int i = 0;
			String line;
			while (i < numLinks && (line = br.readLine()) != null) {
				
				if (line.contains("blog-posts hfeed")) {
					keyFound = true;
				} else if (line.contains("blog-pager")) {
					keyFound = false;
				}
				
				if (keyFound && line.contains("news-title")) {
					newLinks.addFirst(br.readLine().split("'")[1]);
					i++;
				}
			}
			
		} catch (IOException e) {
			logger.error("Failed to retrieve S@20 links", e);
			
		} finally {
			
			// Close out buffered reader
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error("Failed to properly close S220 BufferedReader", e);
				}
			}
		}
		
		return newLinks;
	}
	
}
