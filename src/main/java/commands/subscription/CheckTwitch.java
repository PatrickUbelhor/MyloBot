package commands.subscription;

import io.TwitchRequester;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import main.Bot;
import main.Globals;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import scala.Option;

import static main.Globals.TWITCH_DELAY;
import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 8/21/2017
 */
public class CheckTwitch extends Service {
	
	private static final String OUTPUT_FILE_STREAMERS = "./TwitchStreamers.txt";
	private static final TwitchRequester requester = new TwitchRequester(Globals.TWITCH_CLIENT_ID);
	
	private final LinkedHashMap<String, StreamerInfo> statuses = new LinkedHashMap<>();
	
	CheckTwitch() {
		super("twitch", TWITCH_DELAY, OUTPUT_FILE_STREAMERS);
	}
	
	
	@Override
	protected void parse(String line) {
		if (!line.isEmpty()) {
			
			String[] sections = line.split("\\|");
			String[] streamInfo = sections[0].split(",");
			String[] subscribers = sections[1].split(",");
			
			StreamerInfo info = new StreamerInfo(streamInfo[1], false);
			for (String id : subscribers) {
				info.addSubscriber(Bot.getJDA().getUserById(id));
			}
			
			statuses.put(streamInfo[0], info);
		}
	}
	
	
	@Override
	protected Collection<String> getLines() {
		List<String> lines = new LinkedList<>();
		
		for (String name : statuses.keySet()) {
			StringBuilder line = new StringBuilder();
			
			line.append(name);
			line.append(',');
			line.append(statuses.get(name).id);
			line.append('|');
			
			for (User u : statuses.get(name).getSubscribers()) {
				line.append(u.getId());
				line.append(',');
			}
			line.deleteCharAt(line.length() - 1); // Deletes dangling comma
			
			lines.add(line.toString());
		}
		
		return lines;
	}
	
	
	// TODO: Tell user if they are already subscribed
	@Override
	public void subscribe(MessageReceivedEvent event, String source) {
		try {
			boolean startThread = statuses.isEmpty();
			
			if (!statuses.containsKey(source)) {
				StreamerInfo info = new StreamerInfo(requester.getUserId(source), false);
				info.addSubscriber(event.getAuthor());
				statuses.put(source, info);
				
				if (startThread) {
					startThread();
				} else {
					check();
				}
			}
			
		} catch (Exception e) {
			logger.error(String.format("Could not find Twitch streamer '%s'", source));
			event.getTextChannel().sendMessage(String.format("Could not find Twitch streamer '%s'", source)).queue();
		}
	}
	
	
	// TODO: Tell user if they are already unsubbed
	@Override
	public void unsubscribe(MessageReceivedEvent event, String source) {
		if (statuses.containsKey(source)) {
			statuses.get(source).removeSubscriber(event.getAuthor());
			
			if (statuses.get(source).getSubscribers().isEmpty()) {
				statuses.remove(source);
			}
		}
		
		if (statuses.isEmpty()) {
			endThread();
		}
	}
	
	
	@Override
	protected List<MessageContent> check() {
		List<MessageContent> messageContents = new LinkedList<>();
		
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
					messageContents.add(new MessageContent(link.get(), stream.getSubscribers()));
				}
			}
		}
		
		return messageContents;
	}
	
	
	private class StreamerInfo extends SourceInfo {
		
		private final String id;
		private boolean status;
		
		private StreamerInfo(String id, boolean status) {
			this.id = id;
			this.status = status;
		}
	}
	
}
