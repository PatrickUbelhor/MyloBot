package main;

/**
 * @author Patrick Ubelhor
 * @version 06/26/2018
 */
public class Token {
	
	private final TokenType type;
	private final String data;
	
	public Token(TokenType type, String data) {
		this.type = type;
		this.data = data;
	}
	
	public TokenType getType() {
		return type;
	}
	
	public String getData() {
		return data;
	}
	
}
