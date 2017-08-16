package commands.subscription;

import io.TwitchRequester;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import main.Globals;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import scala.Option;

import static main.Globals.TWITCH_DELAY;
import static main.Globals.logger;

/**
 * @author PatrickUbelhor
 * @version 8/16/2017
 */
public class CheckTwitch extends Service {
	
	private static final String OUTPUT_FILE_STREAMERS = "./TwitchStreamers.txt";
	private static final LinkedHashMap<String, StreamerInfo> statuses = new LinkedHashMap<>();
	private static final TwitchRequester requester = new TwitchRequester(Globals.TWITCH_CLIENT_ID);
	
	CheckTwitch() {
		super("twitch", TWITCH_DELAY, OUTPUT_FILE_STREAMERS);
	}
	
	
	@Override
	protected void parse(String line) {
		if (!line.isEmpty()) {
			String[] tokens = line.split(",");
			statuses.put(tokens[0], new StreamerInfo(tokens[1], false));
		}
	}
	
	
	@Override
	protected Collection<String> getLines() {
		List<String> lines = new LinkedList<>();
		
		for (String name : statuses.keySet()) {
			lines.add(name + "," + statuses.get(name).id);
		}
		
		return lines;
	}
	
	
	@Override
	public void subscribe(MessageReceivedEvent event, String source) {
		try {
			if (!statuses.containsKey(source)) {
				statuses.put(source, new StreamerInfo(requester.getUserId(source), false));
				check();
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
	protected List<String> check() {
		List<String> links = new LinkedList<>();
		
		logger.debug("Checking twitch");
		
		// If streamer was offline last time and is now online, post message
		for (StreamerInfo stream : statuses.values()) {
			logger.debug(String.format("Found streamer: '%s'", stream.id));
			Option<String> link = requester.getStream(stream.id);
			
			if (stream.status) { // If the stream was last online and now isn't, set status to false
				if (link.isEmpty()) {
					stream.status = false;
				}
				
			} else { // If the stream was last offline and now isn't, set status to true
				if (link.nonEmpty()) {
					stream.status = true;
					links.add(link.get());
				}
			}
		}
		
		return links;
	}
	
	
	private class StreamerInfo {
		
		private final String id;
		private boolean status;
		
		private StreamerInfo(String id, boolean status) {
			this.id = id;
			this.status = status;
		}
	}
	
}
