package commands;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author PatrickUbelhor
 * @version 02/16/2017
 */
public class CheckSurrender extends Command {
	
	// TODO: Make it remember what channel subscribed to this when the bot boots up
	// TODO: "!check add" subscribes channel
	// TODO: "!check remove" unsubscribes channel
	// TODO: "!check" manually checks for an update
	
	private static ArrayList<MessageChannel> activeChannels = new ArrayList<>();
	private Checker checker = new Checker();
	
	
	public boolean subInit() {
		try {
			File file = new File("./SurrenderUpdates.txt");
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	public void run(MessageReceivedEvent event, String[] args) {
		
		MessageChannel channel = event.getChannel();
		
		if (activeChannels.contains(channel)) {
			activeChannels.remove(channel);
			if (activeChannels.isEmpty()) {
				checker.interrupt();
			}
			channel.sendMessage("Removing channel from S@20 update queue").queue();
		} else {
			activeChannels.add(channel);
			if (!checker.isAlive()) {
				checker = new Checker();
				checker.start();
			}
			channel.sendMessage("Adding channel to S@20 update queue").queue();
		}
		
	}
	
	
	public String getUsage() {
		return "check";
	}
	
	
	public String getDescription() {
		return "Adds/removes the channel from a queue to receive updates\n\t" +
				"when a new post is made on Surrender@20";
	}
	
	
	private class Checker extends Thread {
		
		public void run() {
			boolean isActive = true;
			
			while (isActive) {
				
				String[] results = check();
				
				for (String result : results) {
					if (result != null) {
						for (MessageChannel c : activeChannels) {
							c.sendMessage(result).queue();
						}
					}
				}
				
				
				try {
					Thread.sleep(10800000); // Sleeps for 3 hours
				} catch (InterruptedException e) {
					System.out.println("Update thread killed");
					isActive = false;
				}
			}
		}
		
		
		private String[] check() {
			
			System.out.println("Checking S@20...");
			
			URL url;
			InputStream is;
			BufferedReader br;
			FileWriter fw;
			String[] lines = new String[3];
			String previousResult = "";
			boolean keyFound = false;
			
			
			try {
				url = new URL("http://www.surrenderat20.net/search/label/Releases/");
				is = url.openStream();
				br = new BufferedReader(new InputStreamReader(is));
				fw = new FileWriter("./SurrenderUpdates.txt", true);
				
				
				// Fetches the links from S@20 webpage
				int i = lines.length - 1;
				while (i >= 0 && (lines[i] = br.readLine()) != null) {
					
					if (lines[i].contains("blog-posts hfeed")) {
						keyFound = true;
					} else if (lines[i].contains("blog-pager")) {
						keyFound = false;
					}
					
					if (lines[i].contains("news-title") && keyFound) {
						lines[i] = br.readLine();
						lines[i] = lines[i].split("\'")[1];
						
						if (i == lines.length) {
							break;
						}
						
						i--;
					}
				}
				
				
				// Checks to see if the link has already been posted
				br = new BufferedReader(new FileReader("./SurrenderUpdates.txt"));
				for (i = 0; i < lines.length; i++) {
					while ((previousResult = br.readLine()) != null) {
						if (previousResult.equals(lines[i])) {
							lines[i] = null;
							break;
						}
					}
					
					if (lines[i] != null) {
						fw.append(lines[i]);
						fw.append("\n");
					}
				}
				
				fw.flush();
				fw.close();
				is.close();
				br.close();
				
				System.out.println("Done!");
				
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			
			return lines;
		}
		
	}
	
}
