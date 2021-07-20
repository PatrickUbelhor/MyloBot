package lib.main;

/**
 * @author Patrick Ubelhor
 * @version 7/19/2021
 */
public enum TokenType {
	
	WHITESPACE("\\s"),
	COMMAND("^![a-zA-Z]+"),
	LINK("https?://.+"),
	WORD("[a-zA-Z]+[\\w\\.':;/?!]*"),
	DICEROLL("[0-9]+d[0-9]+"),
	NUMBER("-?[0-9]+"),
	QUOTE("\".*\""),
	AMP("&$"),
	REMAINDER(".+$");
	
	public final String pattern;
	
	TokenType(String pattern) {
		this.pattern = pattern;
	}
	
}
