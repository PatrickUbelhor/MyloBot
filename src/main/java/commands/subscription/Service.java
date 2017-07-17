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

import static main.Globals.logger;

/**
 * @author PatrickUbelhor
 * @version 7/16/2017
 */
abstract class Service {
	
	private static final LinkedHashMap<String, Service> services = new LinkedHashMap<>(4, 1f);
	// TODO: Static method to load all services
	static CheckSurrender cs = new CheckSurrender();
	
	private final String OUTPUT_FILE_IDS;
	private final String ID_DELIMITER = ",";
	private final LinkedList<MessageChannel> activeChannels = new LinkedList<>();
	private final LinkedList<ID> channelIds = new LinkedList<>();
	private CheckerThread checker = getNewCheckerThread();
	
	protected Service(String subscriberFile) {
		OUTPUT_FILE_IDS = subscriberFile;
		services.put(getName(), this);
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
					logger.info(String.format("Found subscriber: %s|%s\n", guild.getName(), tc.getName()));

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
		
		// Check if channel is already subscribed (also check if source is already listed)
		if (activeChannels.contains(channel)) {
			if (args.length < 3 || containsSource(args[2])) {
				return "Channel is already subscribed";
			}
		}
		
		
		// Validate source
		if (args.length > 2 && !addSource(args[2])) {
			return String.format("'%s' is not a valid source", args[2]);
		}
		
		
		// Add channel (and source) to queue
		activeChannels.add(channel);
		channelIds.add(new ID(event.getGuild().getId(), channel.getId()));
		if (!checker.isAlive()) {
			checker = getNewCheckerThread();
			checker.start();
		}
		
		return String.format("Adding channel to '%s' update queue", getName());
	}
	
	
	final String unsubscribe(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		
		// Ensures the channel is subscribed AND the source is enlisted
		if (!activeChannels.contains(channel)) {
			if (args.length < 3 || !containsSource(args[2])) {
				return "Channel is not subscribed";
			}
		}
		
		if (args.length < 3 || removeSource(args[2])) {
			activeChannels.remove(channel);
			channelIds.remove(new ID(event.getGuild().getId(), channel.getId()));
			if (activeChannels.isEmpty()) {
				checker.interrupt();
			}
		}
		
		return String.format("Removing channel from '%s' update queue", getName());
	}
	
	
	/**
	 * Checks to see if this source has been added to the checking queue.
	 *
	 * @param source Ex. a Twitch or YouTube username.
	 * @return Whether this source has already been entered into the queue.
	 */
	protected boolean containsSource(String source) {
		return true;
	}
	
	
	/**
	 * Checks if a source is valid. If so, adds the source to the source list.
	 *
	 * @param source Ex. a Twitch or YouTube username.
	 * @return Whether or not the source is valid.
	 */
	protected boolean addSource(String source) {
		return true;
	}
	
	
	/**
	 * Removes a source from the update queue.
	 *
	 * @param source Ex. a Twitch or YouTube username.
	 * @return Whether the queue is empty after removing this source.
	 */
	protected boolean removeSource(String source) {
		return true;
	}
	
	
	// TODO: Make alertSubscribers() method to replace this
	protected final List<MessageChannel> getActiveChannels() {
		return activeChannels;
	}
	
	
	protected abstract String getName();
	protected abstract CheckerThread getNewCheckerThread();
	protected abstract boolean loadData();
	
}
