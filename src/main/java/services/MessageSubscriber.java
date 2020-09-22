package services;

import main.Bot;
import main.Globals;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 9/22/2020
 */
public class MessageSubscriber {
	
	private static MessageSubscriber messageSubscriber = null;
	
	public static MessageSubscriber getInstance() {
		if (messageSubscriber == null) {
			messageSubscriber = new MessageSubscriber();
		}
		
		return messageSubscriber;
	}
	
	
	private HashMap<String, LinkedList<Subscriber>> subscribers;
	
	public MessageSubscriber() {
		this.subscribers = new HashMap<>();
	}
	
	
	public void sendMessage(String topic, String message) {
		Iterator<Subscriber> subIterator = subscribers.getOrDefault(topic, new LinkedList<>()).iterator();
		while (subIterator.hasNext()) {
			TextChannel channel = Bot.getJDA().getTextChannelById(subIterator.next().getChannelSnowflake());
			
			if (channel == null) {
				subIterator.remove();
				continue;
			}
			
			channel.sendMessage(message).queue();
		}
	}
	
	
	// TODO: This currently allows a channel to be subbed to a service multiple times (change list to set?)
	public void addSubscriber(String topic, Subscriber sub) {
		subscribers.putIfAbsent(topic, new LinkedList<>());
		subscribers.get(topic).addLast(sub);
		this.saveSubscribers();
	}
	
	
	public void removeSubscriber(String topic, Subscriber sub) {
		if (!subscribers.containsKey(topic)) {
			return;
		}
		
		subscribers.get(topic).remove(sub);
		this.saveSubscribers();
	}
	
	
	// service:sub1,sub2,sub3\n
	private void saveSubscribers() {
		StringBuilder sb = new StringBuilder();
		for (String service : subscribers.keySet()) {
			sb.append(service);
			sb.append(":");
			
			// Don't bother saving services with no subscribers
			// This may occur when a service was previously subbed, but everyone unsubscribed
			if (subscribers.get(service).isEmpty()) {
				continue;
			}
			
			for (Subscriber sub : subscribers.get(service)) {
				sb.append(sub.getChannelSnowflake());
				sb.append(",");
			}
			sb.deleteCharAt(sb.length() - 1); // Remove trailing ","
			sb.append("\n");
		}
		sb.deleteCharAt(sb.length() - 1); // Remove trailing "\n"
		
		// Save to file
		try (FileWriter fw = new FileWriter(Globals.SERVICES_SAVE_PATH, false)) {
			fw.append(sb.toString());
		} catch (IOException e) {
			logger.error("Failed to save service subscribers", e);
		}
	}
	
	
	public void loadSubscribers() {
		
		File saveFile = new File(Globals.SERVICES_SAVE_PATH);
		if (!saveFile.exists()) {
			return;
		}
		
		// Read lines from file
		String[] lines;
		try (BufferedReader br = new BufferedReader(new FileReader(saveFile))) {
			lines = br.lines().toArray(String[]::new);
		} catch (IOException e) {
			logger.error("Failed to load service subscribers", e);
			return;
		}
		
		// Parse lines
		for (String line : lines) {
			String[] splitLine = line.split(":");
			String serviceName = splitLine[0];
			String[] channelSnowflakes = splitLine[1].split(",");
			
			for (String channelSnowflake : channelSnowflakes) {
				long snowflake = Long.parseLong(channelSnowflake);
				Subscriber sub = new Subscriber(snowflake);
				this.addSubscriber(serviceName, sub);
			}
		}
	}
	
}
