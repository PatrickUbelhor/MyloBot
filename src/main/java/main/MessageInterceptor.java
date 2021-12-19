package main;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.util.Random;

/**
 * @author Patrick Ubelhor
 * @version 12/19/2021
 */
public class MessageInterceptor {
	
	private static final String THUMBS_DOWN = "U+1F44E";
	private static final String[] LEAGUE_WORDS = {
			"league", "leage", "leege", "leag", "leegue", "leeg", "lege",
			"reague", "reage", "reege", "reag", "reegue", "reeg", "rege",
			"aram", "norms"
	};
	
	
	public void intercept(MessageReceivedEvent event) {
		User author = event.getAuthor();
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();
		TextChannel ch = event.getTextChannel();
		String msg = message.getContentDisplay().trim();
		
		interceptAtEveryone(message, channel);
		interceptEvanPost(author, ch);
		interceptDavidMeme(author, ch, msg);
		interceptWhoWouldaThoughtMeme(msg, channel);
		interceptAramMsg(author, message, msg);
	}
	
	
	/**
	 * Post @everyone meme when someone tags @everyone.
	 *
	 * @param message The message sent
	 * @param channel The channel to which the message was sent
	 */
	private void interceptAtEveryone(Message message, MessageChannel channel) {
		if (message.mentionsEveryone()) {
			File[] pics = new File(Globals.AT_EVERYONE_PATH).listFiles();
			
			// Send a text response if no images found
			if (pics == null || pics.length == 0) {
				channel.sendMessage("reeeeEEEEEEEEEEEE E E E E E E E E E E E E E").queue();
				return;
			}
			
			channel.sendFile(pics[new Random().nextInt(pics.length)]).queue();
		}
	}
	
	
	/**
	 * Notify Tyler whenever Evan posts something.
	 *
	 * @param author The author of the message
	 */
	private void interceptEvanPost(User author, TextChannel ch) {
		if (author.getIdLong() == 104652244556718080L) {
			String dm = "Evan just posted in " + ch.getGuild().getName() + "#" + ch.getName() + "."
					+ "\nA citation will be required.";
			PrivateChannel tylerDirectMsg = Bot.getJDA().getUserById(104725353402003456L).openPrivateChannel().complete();
			tylerDirectMsg.sendMessage(dm).queue();
		}
	}
	
	
	/**
	 * Send David-related message to the 'david' thread when prompted.
	 * Inspired by the Walter meme: "I like fire trucks and moster trucks".
	 *
	 * @param author The author of the message
	 * @param ch The channel to which the message was sent
	 * @param msg The content of the message
	 */
	private void interceptDavidMeme(User author, TextChannel ch, String msg) {
		if (!author.isBot() && ch.getName().equals("david")) {
			if (msg.toLowerCase().contains("david")) {
				ch.sendMessage("David").queue();
			}
			
			if (msg.toLowerCase().contains("like")) {
				ch.sendMessage("I like moster trucks and David").queue();
			}
			
			if (msg.toLowerCase().contains("coming") && author.getIdLong() == 104400026993709056L) {
				ch.sendMessage("David is coming").queue();
			}
			
			if (msg.toLowerCase().contains("rick") || msg.toLowerCase().contains("morty")) {
				ch.sendMessage("Rick & Morty").queue();
			}
			
			if (msg.toLowerCase().contains("julia") || msg.toLowerCase().contains("10th letter") || msg.toLowerCase().contains("tenth letter")) {
				ch.sendMessage("Daily reminder").queue();
			}
			
			if (msg.toLowerCase().contains("what") && msg.toLowerCase().contains("is")) {
				ch.sendMessage("me").queue();
			}
			
			if (msg.toLowerCase().contains("jeff")) {
				ch.sendMessage("Mah namma Jeeefffff").queue();
			}
			
			if (msg.toLowerCase().contains("minion") || msg.toLowerCase().contains("minin")) {
				ch.sendMessage("MINION").queue();
			}
		}
	}
	
	
	private void interceptWhoWouldaThoughtMeme(String msg, MessageChannel channel) {
		if (!Globals.ENABLE_WHO_WOULDA_THOUGHT_MEME) return;
		
		String lowercase = msg.toLowerCase();
		
		// We want to check that "who" comes before "thought"
		int indexWho = lowercase.indexOf("who");
		if (indexWho == -1) return; // "who" not found
		
		// Will also proc when someone says "Who thought this was a good idea?", but I don't care
		if (lowercase.indexOf("thought", indexWho) != -1) {
			channel.sendMessage("Not me!").queue();
		}
	}
	
	
	private void interceptAramMsg(User author, Message message, String msg) {
		// Avoid responding to legit conversation
		if (msg.length() > 36 || author.getIdLong() != 130424245917319168L) {
			return;
		}
		
		String lowercase = msg.toLowerCase()
				.replace('1', 'l')
				.replace('3', 'e')
				.replace('4', 'a')
				.replace('6', 'g')
				.replace('7', 'l')
				.replace('8', 'g');
		for (String word : LEAGUE_WORDS) {
			if (lowercase.contains(word)) {
				message.addReaction(THUMBS_DOWN).queue(); // Thumbs down
				break;
			}
		}
	}
	
}
