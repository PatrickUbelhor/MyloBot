package commands;

import main.Bot;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author PatrickUbelhor
 * @version 6/3/2017
 */
public class CheckSurrender extends Command {
	
	private static final int NUM_UPDATES = 3;
	private static final long DELAY_TIME = 10800000; // Time between checks, in ms. 3 hours.
	private static final String OUTPUT_FILE_LINKS = "./SurrenderUpdates.txt";
	private static final String OUTPUT_FILE_IDS = "./ChannelIDs.txt";
	
	private static ArrayList<MessageChannel> activeChannels = new ArrayList<>();
	private static ArrayList<Long> channelIDs = new ArrayList<>();
	private static String[] oldLinks = new String[NUM_UPDATES];
	private Checker checker = new Checker();
	
	
	public boolean subInit() {
		BufferedReader br = null;
		String line = null;
		
		try {
			File links = new File(OUTPUT_FILE_LINKS);
			if (!links.createNewFile()) {
				br = new BufferedReader(new FileReader(OUTPUT_FILE_LINKS));
				
				while ((line = br.readLine()) != null) {
					addLink(line);
				}
			}
			
			File ids = new File(OUTPUT_FILE_IDS);
			if (!ids.createNewFile()) {
				br = new BufferedReader(new FileReader(OUTPUT_FILE_IDS));
				
				while ((line = br.readLine()) != null) {
					channelIDs.add(Long.parseLong(line));
					activeChannels.add(Bot.getJDA().getTextChannelById(Long.parseLong(line)));
				}
			}
			
			if (br != null) br.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	public void run(MessageReceivedEvent event, String[] args) {
		
		MessageChannel channel = event.getChannel();
		channelIDs.add(channel.getIdLong());
		
		if (args.length > 1) {
			switch (args[1].toLowerCase()) {
				
				case "add":
					if (activeChannels.contains(channel)) {
						channel.sendMessage("Channel is already subscribed").queue();
					} else {
						activeChannels.add(channel);
						if (!checker.isAlive()) {
							checker = new Checker();
							checker.start();
						}
						channel.sendMessage("Adding channel to S@20 update queue").queue();
					}
					break;
					
				case "remove":
					if (activeChannels.contains(channel)) {
						activeChannels.remove(channel);
						if (activeChannels.isEmpty()) {
							checker.interrupt();
						}
						channel.sendMessage("Removing channel from S@20 update queue").queue();
					} else {
						channel.sendMessage("Channel is not subscribed").queue();
					}
					break;
					
				default:
					checkOnce(channel);
					break;
			}
		} else {
			checkOnce(channel);
		}
		
	}
	
	
	public boolean subEnd() {
		
		try (FileWriter fw = new FileWriter(OUTPUT_FILE_IDS, false)) {
			for (long id : channelIDs) {
				fw.append(Long.toString(id));
				fw.append('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	public String getUsage() {
		return "check [add/remove]";
	}
	
	
	public String getDescription() {
		return "Adds/removes the channel from a queue to receive updates\n\t" +
				"when a new post is made on Surrender@20. No optional\n\t" +
				"will manually check the feed once.";
	}
	
	
	private void checkOnce(MessageChannel channel) {
		boolean foundResult = false;
		for (String s : checker.check()) {
			if (s != null) {
				channel.sendMessage(s).queue();
				foundResult = true;
			}
		}
		
		if (!foundResult) {
			channel.sendMessage("No updates were found!").queue();
		}
	}
	
	
	private void addLink(String link) {
		System.arraycopy(oldLinks, 0, oldLinks, 1, oldLinks.length);
		oldLinks[0] = link;
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
					Thread.sleep(DELAY_TIME); // Sleeps for 3 hours
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
			String[] newLinks = new String[NUM_UPDATES];
			String previousResult = "";
			boolean keyFound = false;
			
			
			try {
				url = new URL("http://www.surrenderat20.net/search/label/Releases/");
				is = url.openStream();
				br = new BufferedReader(new InputStreamReader(is));
				fw = new FileWriter(OUTPUT_FILE_LINKS, true);
				
				
				// Fetches the links from S@20 webpage
				int i = newLinks.length - 1;
				while (i >= 0 && (newLinks[i] = br.readLine()) != null) {
					
					if (newLinks[i].contains("blog-posts hfeed")) {
						keyFound = true;
					} else if (newLinks[i].contains("blog-pager")) {
						keyFound = false;
					}
					
					if (newLinks[i].contains("news-title") && keyFound) {
						newLinks[i] = br.readLine();
						newLinks[i] = newLinks[i].split("\'")[1];
						
						if (i == newLinks.length) {
							break;
						}
						
						i--;
					}
				}
				
				
				// Checks to see if the link has already been posted
				for (int j = 0; j < newLinks.length; j++) {
					for (String oldLink : oldLinks) {
						if (newLinks[j].equals(oldLink)) {
							newLinks[j] = null;
							break;
						}
					}
					
					if (newLinks[i] != null) {
						fw.append(newLinks[i]);
						fw.append("\n");
					}
				}
				
				
				// Updates the old links
				for (String link : newLinks) {
					if (link != null) {
						addLink(link);
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
			
			return newLinks;
		}
	}
	
}
