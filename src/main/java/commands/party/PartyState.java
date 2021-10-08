package commands.party;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Patrick Ubelhor
 * @version 9/30/2021
 */
class PartyState {
	private final String name;
	private final HashSet<Long> originalMembers;
	private final HashSet<Long> notifiedMembers;
	
	PartyState(String name, Collection<Long> members) {
		this.name = name;
		this.originalMembers = new HashSet<>(members);
		this.notifiedMembers = new HashSet<>(members);
	}
	
	String getName() {
		return name;
	}
	
	HashSet<Long> getOriginalMembers() {
		return originalMembers;
	}
	
	HashSet<Long> getNotifiedMembers() {
		return notifiedMembers;
	}
	
	void addNotifiedMember(Long memberId) {
		notifiedMembers.add(memberId);
	}
	
}
