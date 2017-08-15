package commands.subscription;

import com.google.common.collect.LinkedListMultimap;
import java.io.IOException;
import java.util.LinkedHashMap;
import main.Bot;
import main.Globals;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import static main.Globals.logger;

/**
 * @author PatrickUbelhor
 * @version 8/15/2017
 */
public abstract class Service {
	
	private static final LinkedHashMap<String, Service> serviceMap = new LinkedHashMap<>();
	private static MessageChannel mediaChannel;
	
	private final LinkedListMultimap<String, User> subscribers = LinkedListMultimap.create();
	private final String name;
	
	
	public static LinkedHashMap<String, Service> getServiceMap() {
		return serviceMap;
	}
	
	
	Service(String name) {
		this.name = name;
		serviceMap.put(name, this);
	}
	
	
	// TODO: make init a static method
	public void init() {
		if (mediaChannel == null) {
			mediaChannel = Bot.getJDA().getTextChannelById(Globals.MEDIA_CHANNEL_ID);
			logger.debug("Found media channel: " + mediaChannel.getName());
		}
		
		if (!subInit()) {
			serviceMap.remove(name);
			System.err.printf("\tFailed to initialize !%s\n", name);
		} else {
			System.out.printf("\tInitialized !%s\n", name);
		}
	}
	
	
	public void end() {
		if (!subEnd()) {
			System.out.printf("Module %s failed to shut down properly!", name);
		}
	}
	
	
	/**
	 * Subscribes a user to the pool of users to '@mention' when a source is updated. If this is the first subscriber
	 * to this service, this service's update thread is initialized.
	 *
	 * @param source The source to subscribe to.
	 * @param user The user that is subscribing.
	 */
	public void subscribe(String source, User user) {
		boolean startThread = subscribers.isEmpty();
		logger.debug("Calling service: " + this.getName());
		
		logger.debug(String.format("Putting source '%s' and user '%s' into map", source, user.getName()));
		subscribers.put(source, user);
		
		if (startThread) {
			startThread();
		}
		
	}
	
	
	/**
	 * Unsubscribes a user from the pool of users to '@mention' when a source is updated. If there are no subscribers
	 * remaining in this service, the service's update thread is killed.
	 *
	 * @param source The source to unsubscribe from.
	 * @param user The user that is unsubscribing.
	 */
	public void unsubscribe(String source, User user) {
		subscribers.remove(source, user);
		
		if (subscribers.isEmpty()) {
			try {
				endThread();
			} catch (IOException e) {
				logger.error(String.format("Could not end %s CheckerThread", name), e);
			}
		}
	}
	
	
	/**
	 * @return The name of this service.
	 */
	public final String getName() {
		return name;
	}
	
	
	/**
	 * @return The guild's text channel onto which updates should be posted.
	 */
	protected MessageChannel getMediaChannel() {
		return mediaChannel;
	}
	
	
	/**
	 * @return The mapping of sources to the list of subscribed users for said source.
	 */
	protected LinkedListMultimap<String, User> getSubscribers() {
		return subscribers;
	}
	
	
	protected abstract boolean subInit();
	protected abstract boolean subEnd();
	protected abstract void startThread();
	protected abstract void endThread() throws IOException;
	
}
