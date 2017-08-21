package commands.subscription;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import main.Bot;
import main.Globals;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import static main.Globals.logger;

/**
 * @author PatrickUbelhor
 * @version 8/21/2017
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
			if (s.load()) {
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
			
			if (!s.save()) {
				logger.error(String.format("Module %s failed to shut down properly!", s.name));
			}
		}
	}
	
	
//	private final LinkedHashMap<String, SourceInfo> sources = new LinkedHashMap<>();
	private final String name;
	private final long delayTime;
	private final String saveFilePath;
	private CheckerThread thread = null;
	
	
	Service(String name, long delayTime, String saveFilePath) {
		this.name = name;
		this.delayTime = delayTime;
		this.saveFilePath = saveFilePath;
	}
	
	
	/**
	 * @return The name of this service.
	 */
	public final String getName() {
		return name;
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
	
	
	/**
	 * Parses the save file for sources.
	 *
	 * @return True if the file is successfully parsed.
	 */
	protected final boolean load() {
		boolean isFreshFile;
		
		// Attempt to create save file if it doesn't exist
		try {
			File file = new File(saveFilePath);
			isFreshFile = file.createNewFile();
		} catch (IOException e) {
			logger.error(String.format("Failed to create file: %s", saveFilePath));
			return false;
		}
		
		// If this file wasn't just created, parse it and start thread
		boolean hasActiveSource = false;
		if (!isFreshFile) {
			try (BufferedReader br = new BufferedReader(new FileReader(saveFilePath))) {
				
				String line;
				while ((line = br.readLine()) != null) {
					if (!line.isEmpty()) {
						parse(line);
						hasActiveSource = true;
					}
				}
				
			} catch (IOException e) {
				logger.error(String.format("Failed to read file: '%s'", saveFilePath), e);
				return false;
			}
			
			
			if (hasActiveSource) {
				startThread();
			}
		}
		
		return true;
	}
	
	
	/**
	 * Save data to file.
	 *
	 * @return True if data was successfully saved.
	 */
	protected final boolean save() {
		try (FileWriter fw = new FileWriter(saveFilePath, false)){
			
			for (String s : getLines()) {
				fw.append(s);
				fw.append('\n');
			}
			
		} catch (IOException e) {
			logger.error("Failed to save streamers", e);
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * Subscribes a user to the pool of users to '@mention' when a source is updated. If this is the first subscriber
	 * to this service, this service's update thread is initialized.
	 *
	 * @param event The message event that requested a subscription to a source.
	 * @param source The source to subscribe to.
	 */
	public abstract void subscribe(MessageReceivedEvent event, String source);
	
	
	/**
	 * Unsubscribes a user from the pool of users to '@mention' when a source is updated. If there are no sources
	 * remaining in this service, the service's update thread is killed.
	 *
	 * @param event The message event that requested to cancel a subscription.
	 * @param source The source to unsubscribe from.
	 */
	public abstract void unsubscribe(MessageReceivedEvent event, String source);
	
	
	protected abstract void parse(String line);
	protected abstract Collection<String> getLines();
	protected abstract List<MessageContent> check();
	
}
