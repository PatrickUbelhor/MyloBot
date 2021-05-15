package lib.exception;

/**
 * @author Patrick Ubelhor
 * @version 5/15/2021
 */
public class LoadConfigException extends RuntimeException {
	
	public LoadConfigException(Throwable cause) {
		super(cause);
	}
	
	
	public LoadConfigException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
