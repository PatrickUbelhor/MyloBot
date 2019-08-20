package commands.subscription;

import clients.SurrenderClient;
import main.Globals;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static main.Globals.SURRENDER_DELAY;
import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 6/18/2019
 */
public class CheckSurrender extends Service {
	
	private static final int NUM_UPDATES = 3;
	private static final String OUTPUT_FILE_LINKS = Globals.SURRENDER_20_PATH;
	private static final SourceInfo sourceInfo = new SourceInfo();
	
	private SurrenderClient client = new SurrenderClient();
	private LinkedList<String> oldLinks = new LinkedList<>();
	
	CheckSurrender() {
		super("surrender", SURRENDER_DELAY, OUTPUT_FILE_LINKS);
	}
	
	
	@Override
	protected void parse(String line) {
		oldLinks.add(line);
	}
	
	
	@Override
	protected Collection<String> getLines() {
		return oldLinks;
	}
	
	
	// TODO: Tell user if already subbed
	@Override
	public void subscribe(MessageReceivedEvent event, String source) {
		// Only want to start thread if this is the first subscriber
		boolean startThread = sourceInfo.getSubscribers().isEmpty();
		
		sourceInfo.addSubscriber(event.getAuthor());
		
		if (startThread) {
			startThread();
		}
	}
	
	
	// TODO: Tell user if already unsubbed
	@Override
	public void unsubscribe(MessageReceivedEvent event, String source) {
		sourceInfo.removeSubscriber(event.getAuthor());
		
		if (sourceInfo.getSubscribers().isEmpty()) {
			endThread();
		}
	}
	
	
	@Override
	protected List<MessageContent> check() {
		
		List<String> newLinks = client.getLinks(NUM_UPDATES);
		if (newLinks.isEmpty()) return new LinkedList<>();
		
		newLinks.removeAll(oldLinks);
		
		// Updates the old links. Removes oldest links to maintain constant size.
		for (String link : newLinks) {
			oldLinks.addFirst(link);
			oldLinks.removeLast();
		}
		
		// Attempt to save new links to file
		try (FileWriter fw = new FileWriter(OUTPUT_FILE_LINKS)) {
			
			// Writes new links to file
			for (String link : newLinks) {
				fw.append(link);
				fw.append("\n");
			}
			
		} catch (IOException e) {
			logger.error("Failed to write S@20 links to file.", e);
		}
		
		
		// Create MessageContent objects to return for Discord message.
		List<MessageContent> messageContents = new LinkedList<>();
		for (String s : newLinks) {
			messageContents.add(new MessageContent(s, sourceInfo.getSubscribers()));
		}
		
		return messageContents;
	}
	
}
