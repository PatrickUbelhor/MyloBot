package services;

import main.Bot;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Patrick Ubelhor
 * @version 2/27/2020
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
	
	
	public void addSubscriber(String topic, Subscriber sub) {
		subscribers.putIfAbsent(topic, new LinkedList<>());
		subscribers.get(topic).addLast(sub);
	}
	
}
