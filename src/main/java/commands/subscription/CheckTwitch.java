package commands.subscription;

import io.TwitchRequester;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import main.Globals;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import scala.Option;

import static main.Globals.TWITCH_DELAY;
import static main.Globals.logger;

/**
 * @author PatrickUbelhor
 * @version 8/15/2017
 */
public class CheckTwitch extends Service {
	
	private static final String OUTPUT_FILE_STREAMERS = "./TwitchStreamers.txt";
	private static final LinkedHashMap<String, Boolean> statuses = new LinkedHashMap<>();
	private static final TwitchRequester requester = new TwitchRequester(Globals.TWITCH_CLIENT_ID);
	private static CheckTwitchThread thread = null;
	
	CheckTwitch() {
		super("twitch");
	}
	
	
	@Override
	protected boolean subInit() {
		String[] lines;
		
		// Create save file if it doesn't exist, and parse save file if it does
		if (!createFile(OUTPUT_FILE_STREAMERS)) {
			lines = getLines(OUTPUT_FILE_STREAMERS);
			
			if (lines == null) return false;
			
			for (String line : lines) {
				statuses.put(line, false);
			}
			
			thread = new CheckTwitchThread();
			thread.start();
		}
		
		return true;
	}
	
	
	@Override
	protected boolean subEnd() {
		try (FileWriter fw = new FileWriter(OUTPUT_FILE_STREAMERS, false)){
			
			for (String s : statuses.keySet()) {
				fw.append(s);
				fw.append('\n');
			}
			
		} catch (IOException e) {
			logger.error("Failed to save streamers", e);
		}
		
		return true;
	}
	
	
	@Override
	public void subscribe(MessageReceivedEvent event, String source) {
		try {
			if (statuses.putIfAbsent(requester.getUserId(source), false) != null) {
				thread.check();
			}
			
			super.subscribe(event, source);
		} catch (Exception e) {
			logger.error(String.format("Could not find Twitch streamer '%s'", source));
			event.getTextChannel().sendMessage(String.format("Could not find Twitch streamer '%s'", source)).queue();
		}
	}
	
	
	@Override
	public void unsubscribe(MessageReceivedEvent event, String source) {
		try {
			statuses.remove(requester.getUserId(source));
			super.unsubscribe(event, source);
		} catch (Exception e) {
			logger.error(String.format("Could not find Twitch streamer '%s'", source));
			event.getTextChannel().sendMessage(String.format("Could not find Twitch streamer '%s'", source)).queue();
		}
	}
	
	
	@Override
	protected void startThread() {
		thread = new CheckTwitchThread();
		thread.start();
	}
	
	
	@Override
	protected void endThread() {
		if (thread != null && thread.isAlive()) {
			thread.interrupt();
		}
	}
	
	
	private class CheckTwitchThread extends CheckerThread {
		
		
		CheckTwitchThread() {
			super(CheckTwitch.class.getSimpleName(), TWITCH_DELAY);
		}
		
		
		protected void check() {
			logger.debug("Checking twitch");
			
			// If streamer was offline last time and is now online, post message
			for (String streamer : statuses.keySet()) {
				logger.debug(String.format("Found streamer: '%s'", streamer));
				Option<String> link = requester.getStream(streamer);
				
				if (statuses.get(streamer)) {
					if (link.isEmpty()) {
						statuses.put(streamer, false);
					}
					
				} else {
					if (link.nonEmpty()) {
						statuses.put(streamer, true);
						getMediaChannel().sendMessage(link.get()).queue();
					} // Else streamer is offline
				}
			}
		}
	
	}
	
}
