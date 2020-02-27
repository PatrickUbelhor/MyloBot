package services;

/**
 * @author Patrick Ubelhor
 * @version 2/27/2020
 */
public class Subscriber {
	
	private final long guildSnowflake;
	private final long channelSnowflake;
	
	public Subscriber(long guildSnowflake, long channelSnowflake) {
		this.guildSnowflake = guildSnowflake;
		this.channelSnowflake = channelSnowflake;
	}
	
	
	public long getGuildSnowflake() {
		return guildSnowflake;
	}
	
	
	public long getChannelSnowflake() {
		return channelSnowflake;
	}
	
}
