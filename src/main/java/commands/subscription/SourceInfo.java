package commands.subscription;

import java.util.Collection;
import java.util.LinkedList;
import net.dv8tion.jda.core.entities.User;

/**
 * @author Patrick Ubelhor
 * @version 08/16/2017
 */
class SourceInfo {
	
	private Collection<User> subscribers = new LinkedList<>();
	
	
	/**
	 * Gets the collection of users that are subscribed to a source.
	 *
	 * @return The collection of subscribed users.
	 */
	Collection<User> getSubscribers() {
		return subscribers;
	}
	
	
	/**
	 * Adds the specified user to the collection of subscribers.
	 *
	 * @param user The user to add to the collection.
	 */
	void addSubscriber(User user) {
		subscribers.add(user);
	}
	
	
	/**
	 * Removes the specified user from the list of subscribers.
	 *
	 * @param user The user to remove from the collection.
	 */
	void removeSubscriber(User user) {
		subscribers.removeIf((p) -> p.getIdLong() == user.getIdLong());
	}
	
}
