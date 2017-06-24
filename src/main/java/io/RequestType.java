package io;

/**
 * @author PatrickUbelhor
 * @version 06/23/2017
 */
enum RequestType {
	
	DELETE("DELETE"),
	GET("GET"),
	POST("POST"),
	PUT("PUT");
	
	private final String val;
	
	RequestType(String val) {
		this.val = val;
	}
	
	public String get() {
		return val;
	}
	
	
}
