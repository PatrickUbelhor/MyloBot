package services;

import clients.SurrenderClient;
import lib.services.MessageSubscriber;
import lib.services.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Patrick Ubelhor
 * @version 3/10/2021
 */
public class SurrenderAt20 extends Service {
	
	// Grab up to 3 links because multiple posts might be made between polls
	private static final int NUM_UPDATES = 3;
	
	private final SurrenderClient client;
	private final LinkedList<String> oldLinks;
	
	public SurrenderAt20(long period) {
		super("surrender", period);
		this.client = new SurrenderClient();
		this.oldLinks = new LinkedList<>();
	}
	
	
	@Override
	protected boolean init() {
		List<String> newLinks = client.getLinks(NUM_UPDATES);
		
		// We want oldest to be last in the list when it gets popped later
		for (String link : newLinks) {
			oldLinks.addLast(link);
		}
		return true;
	}
	
	
	@Override
	protected void execute() {
		List<String> newLinks = client.getLinks(NUM_UPDATES);
		newLinks.removeAll(oldLinks);
		
		// No updates found; yield
		if (newLinks.isEmpty()) {
			return;
		}
		
		// Push messages and update internal store
		for (String link : newLinks) {
			MessageSubscriber.getInstance()
					.sendMessage(this.getName(), link);
			
			oldLinks.addFirst(link); // Update seen links
			oldLinks.removeLast(); // Maintain constant size
		}
	}
	
	
	@Override
	protected boolean end() {
		return true;
	}
	
	
	@Override
	public String getInfo() {
		return "Periodically checks to see if new articles have been posted on Surrender@20.";
	}
	
}
