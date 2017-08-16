package commands.subscription;

import com.google.common.collect.LinkedListMultimap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import main.Bot;
import main.Globals;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import static main.Globals.logger;

/**
 * @author PatrickUbelhor
 * @version 8/16/2017
 * @noinspection WeakerAccess
 */
public abstract class Service {
	
	private static final LinkedHashMap<String, Service> serviceMap = new LinkedHashMap<>();
	private static MessageChannel mediaChannel;
	
	
	public static LinkedHashMap<String, Service> getServiceMap() {
		return serviceMap;
	}
	
	
	public static boolean init() {
		mediaChannel = Bot.getJDA().getTextChannelById(Globals.MEDIA_CHANNEL_ID);
		logger.info("Found media channel: " + mediaChannel.getName());
		
		List<Service> services = new LinkedList<>();
		services.add(new CheckTwitch());
		services.add(new CheckSurrender());
		
		for (Service s : services) {
			if (s.subInit()) {
				serviceMap.put(s.name, s);
				logger.info(String.format("\tInitialized service: %s", s.name));
			} else {
				logger.error(String.format("\tFailed to initialize service: %s", s.name));
			}
		}
		
		return !serviceMap.isEmpty();
	}
	
	
	public static void end() {
		for (Service s : serviceMap.values()) {
			s.endThread();
			
			if (!s.subEnd()) {
				logger.error(String.format("Module %s failed to shut down properly!", s.name));
			}
		}
	}
	
	
	private final LinkedListMultimap<String, User> subscribers = LinkedListMultimap.create();
	private final String name;
	private final long delayTime;
	private CheckerThread thread = null;
	
	
	Service(String name, long delayTime) {
		this.name = name;
		this.delayTime = delayTime;
		serviceMap.put(name, this);
	}
	
	
	/**
	 * Subscribes a user to the pool of users to '@mention' when a source is updated. If this is the first subscriber
	 * to this service, this service's update thread is initialized.
	 *
	 * @param event The message event that requested a subscription to a source.
	 * @param source The source to subscribe to.
	 */
	public void subscribe(MessageReceivedEvent event, String source) {
		boolean startThread = subscribers.isEmpty();
		logger.debug("Calling service: " + this.getName());
		
		logger.debug(String.format("Putting source '%s' and user '%s' into map", source, event.getAuthor().getName()));
		subscribers.put(source, event.getAuthor());
		
		if (startThread) {
			startThread();
		}
	}
	
	
	/**
	 * Unsubscribes a user from the pool of users to '@mention' when a source is updated. If there are no subscribers
	 * remaining in this service, the service's update thread is killed.
	 *
	 * @param event The message event that requested to cancel a subscription.
	 * @param source The source to unsubscribe from.
	 */
	public void unsubscribe(MessageReceivedEvent event, String source) {
		subscribers.remove(source, event.getAuthor());
		
		if (subscribers.isEmpty()) {
			endThread();
		}
	}
	
	
	/**
	 * @return The name of this service.
	 */
	public final String getName() {
		return name;
	}
	
	
	/**
	 * @return The mapping of sources to the list of subscribed users for said source.
	 */
	protected LinkedListMultimap<String, User> getSubscribers() {
		return subscribers;
	}
	
	
	/**
	 * Creates a file with the given path. Name is specified as part of the path. Used to create save file.
	 *
	 * @param path The path of the file to create.
	 * @return True if the file was successfully created.
	 */
	protected boolean createFile(String path) {
		try {
			File file = new File(path);
			return file.createNewFile();
			
		} catch (IOException e) {
			logger.error(String.format("Failed to create file: %s", path));
			return false;
		}
	}
	
	
	/**
	 * Gets an array in which each entry is a line from the file at the specified path.
	 *
	 * @param path The path of the file to read.
	 * @return An array of lines within the file.
	 */
	protected String[] getLines(String path) {
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			
			// Load the Twitch IDs of all the subscribed streamers
			String line;
			StringBuilder sum = new StringBuilder();
			while ((line = br.readLine()) != null) {
				if (!line.isEmpty()) sum.append(line).append('\n');
			}
			
			return sum.toString().split("\n");
		} catch (IOException e) {
			logger.error(String.format("Failed to read file: '%s'", path), e);
			return null;
		}
	}
	
	
	/**
	 * Starts the thread that checks for updates.
	 */
	protected final void startThread() {
		if (thread != null && thread.isAlive()) {
			logger.warn("Tried to start CheckerThread when one was already active!");
			return;
		}
		
		thread = new CheckerThread(this.getClass().getSimpleName(), delayTime, this::check, mediaChannel);
		thread.start();
	}
	
	
	/**
	 * Kills the thread that checks for updates.
	 */
	protected final void endThread() {
		if (thread == null) {
			logger.warn("Cannot kill null CheckerThread: " + name);
			return;
		}
		
		if (!thread.isAlive()) {
			logger.warn("Cannot kill dead CheckerThread: " + name);
			return;
		}
		
		thread.interrupt();
		thread = null;
	}
	
	
	protected abstract boolean subInit();
	protected abstract boolean subEnd();
	protected abstract List<String> check();
	
}
