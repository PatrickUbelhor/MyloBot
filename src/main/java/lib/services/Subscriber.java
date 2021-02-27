package lib.services;

/**
 * @author Patrick Ubelhor
 * @version 2/27/2020
 */
public class Subscriber {
	
	private final long channelSnowflake;
	
	public Subscriber(long channelSnowflake) {
		this.channelSnowflake = channelSnowflake;
	}
	
	
	public long getChannelSnowflake() {
		return channelSnowflake;
	}
	
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Subscriber
				&& ((Subscriber) o).channelSnowflake == this.channelSnowflake;
	}
	
}
