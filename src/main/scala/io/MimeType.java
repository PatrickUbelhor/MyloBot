package io;

/**
 * @author Patrick Ubelhor
 * @version 06/23/2017
 */
enum MimeType {
	
	TWITCH_JSON("application/vnd.twitchtv.v5+json");
	
	
	private final String val;
	
	MimeType(String val) {
		this.val = val;
	}
	
	public String get() {
		return val;
	}
	
}
