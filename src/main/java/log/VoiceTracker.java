package log;

import net.dv8tion.jda.api.entities.User;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Patrick Ubelhor
 * @version 10/31/2019
 */
public class VoiceTracker {
	
	private HashMap<Long, LinkedList<VoiceTrackerEntry>> log;
	
	public VoiceTracker() {
		 log = new HashMap<>();
	}
	
	
	public void enter(User user) {
		log.putIfAbsent(user.getIdLong(), new LinkedList<>());
		
		VoiceTrackerEntry entry = new VoiceTrackerEntry();
		entry.setUsername(user.getName());
		entry.setTimeIn(new Date());
		
		log.get(user.getIdLong()).addLast(entry);
	}
	
	
	public void exit(User user) {
		
		// If users are in call when bot starts up, they won't have an entry log
		if (!log.containsKey(user.getIdLong())) {
			return;
		}
		
		log.get(user.getIdLong()).getLast().setTimeOut(new Date());
	}
	
}
