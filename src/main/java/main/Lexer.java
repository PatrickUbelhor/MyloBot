package main;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Patrick Ubelhor
 * @version 06/26/2018
 */
public class Lexer {
	
	private final Pattern pattern;
	
	public Lexer() {
		StringBuilder patternBuilder = new StringBuilder();
		for (TokenType type : TokenType.values()) {
			patternBuilder.append(String.format("|(?<%s>%s)", type.name(), type.pattern));
		}
		pattern = Pattern.compile(patternBuilder.substring(1)); // Cut off first '|'
		
	}
	
	public List<Token> lex(String input) {
		List<Token> tokens = new ArrayList<>();
		
		Matcher matcher = pattern.matcher(input);
		while (matcher.find()) {
			if (matcher.group(TokenType.WHITESPACE.name()) != null) {
				continue;
			}
			
			for (TokenType type : TokenType.values()) {
				String group = matcher.group(type.name());
				if (group != null) {
					tokens.add(new Token(type, group));
				}
			}
		}
		
		return tokens;
	}
	
}
