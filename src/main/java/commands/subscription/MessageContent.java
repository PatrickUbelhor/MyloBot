package commands.subscription;

import java.util.Collection;
import net.dv8tion.jda.api.entities.User;

/**
 * @author Patrick Ubelhor
 * @version 08/21/2017
 */
class MessageContent {
	
	private final String link;
	private final Collection<User> subscribers;
	
	MessageContent(String link, Collection<User> subscribers) {
		this.link = link;
		this.subscribers = subscribers;
	}
	
	
	String getLink() {
		return link;
	}
	
	
	Collection<User> getSubscribers() {
		return subscribers;
	}
	
}
