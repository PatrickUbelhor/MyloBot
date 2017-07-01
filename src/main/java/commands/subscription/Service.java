package commands.subscription;

import main.Bot;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author PatrickUbelhor
 * @version 06/24/2017
 */
abstract class Service {
	
	private static final LinkedHashMap<String, Service> services = new LinkedHashMap<>(4, 1f);
	// TODO: Static method to load all services
	static CheckSurrender cs2 = new CheckSurrender();
	
	private final String OUTPUT_FILE_IDS;
	private final String ID_DELIMITER = ",";
	private final LinkedList<MessageChannel> activeChannels = new LinkedList<>();
	private final LinkedList<ID> channelIds = new LinkedList<>();
	private CheckerThread checker = getNewCheckerThread();
	
	protected Service(String subscriberFile) {
		OUTPUT_FILE_IDS = subscriberFile;
		services.put(getName(), this);
		if (!services.containsKey(getName())) System.out.println("Not in map");
	}
	
	static LinkedHashMap<String, Service> getServices() {
		return services;
	}
	
	final boolean loadSubscribers() {
		File idFile = new File(OUTPUT_FILE_IDS);
		BufferedReader br = null;

		// Load all subscribed channels
		try {
			if (!idFile.createNewFile()) {
				br = new BufferedReader(new FileReader(OUTPUT_FILE_IDS));

				String line;
				while ((line = br.readLine()) != null) {
					String[] ids = line.split(ID_DELIMITER);
					channelIds.add(new ID(ids[0], ids[1]));

					JDA jda = Bot.getJDA();
					Guild guild = jda.getGuildById(ids[0]);
					TextChannel tc = jda.getTextChannelById(ids[1]);
					System.out.printf("Found subscriber: %s|%s\n", guild.getName(), tc.getName());

					activeChannels.add(tc);
				}

				if (!activeChannels.isEmpty()) {
					checker.start();
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
	
	
	protected final boolean saveSubscribers() {
		
		try (FileWriter fw = new FileWriter(OUTPUT_FILE_IDS, false)) {
			for (ID id : channelIds) {
				fw.append(id.getGuildID());
				fw.append(ID_DELIMITER);
				fw.append(id.getChannelID());
				fw.append('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	final boolean end() {
		if (checker.isAlive()) {
			checker.interrupt();
		}
		
		return saveSubscribers();
	}
	
	
	final String subscribe(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		
		if (activeChannels.contains(channel)) {
			return "Channel is already subscribed";
		}
		
		activeChannels.add(channel);
		channelIds.add(new ID(event.getGuild().getId(), channel.getId()));
		if (!checker.isAlive()) {
			checker = getNewCheckerThread();
			checker.start();
		}
		
		return String.format("Adding channel to %s update queue", getName());
	}
	
	
	final String unsubscribe(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		
		if (!activeChannels.contains(channel)) {
			return "Channel is not subscribed";
		}
		
		activeChannels.remove(channel);
		channelIds.remove(new ID(event.getGuild().getId(), channel.getId()));
		if (activeChannels.isEmpty()) {
			checker.interrupt();
		}
		
		return String.format("Removing channel from %s update queue", getName());
	}
	
	
	protected List<MessageChannel> getActiveChannels() {
		return activeChannels;
	}
	
	
	protected abstract String getName();
	protected abstract CheckerThread getNewCheckerThread();
	protected abstract boolean loadData();
	
}
