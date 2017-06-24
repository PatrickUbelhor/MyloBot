package commands.subscription;

/**
 * @author PatrickUbelhor
 * @version 06/24/2017
 */
class ID {
	
	private final String guildID;
	private final String channelID;
	
	ID(String guildID, String channelID) {
		this.guildID = guildID;
		this.channelID = channelID;
	}
	
	String getGuildID() {
		return guildID;
	}
	
	String getChannelID() {
		return channelID;
	}
	
	@Override
	public boolean equals(Object id) {
		return (id.getClass() == this.getClass() &&
		        this.guildID.equals(((ID) id).guildID) &&
		        this.channelID.equals(((ID) id).channelID));
	}
	
}
