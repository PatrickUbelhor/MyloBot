package main;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.util.Random;

/**
 * @author Patrick Ubelhor
 * @version 12/9/2024
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
		GuildMessageChannel ch = event.getGuildChannel();
		String text = message.getContentDisplay().trim();
		String lowercaseText = text.toLowerCase();

		interceptAtEveryone(message, channel);
		interceptEvanPost(author, ch);
		interceptDavidMeme(author, channel, lowercaseText);
		interceptWhoWouldaThoughtMeme(text, channel);
		interceptAramMsg(author, message, text);
		interceptTwitterLink(message, text);
		interceptMudaeBotRoll(message, text);
	}


	/**
	 * Post @everyone meme when someone tags @everyone.
	 *
	 * @param message The message sent
	 * @param channel The channel to which the message was sent
	 */
	private void interceptAtEveryone(Message message, MessageChannel channel) {
		if (message.getMentions().mentionsEveryone()) {
			File[] pics = new File(Config.getConfig().AT_EVERYONE_PATH()).listFiles();

			// Send a text response if no images found
			if (pics == null || pics.length == 0) {
				channel.sendMessage("reeeeEEEEEEEEEEEE E E E E E E E E E E E E E").queue();
				return;
			}

			File file = pics[new Random().nextInt(pics.length)];
			FileUpload fileUpload = FileUpload.fromData(file);
			channel.sendFiles(fileUpload).queue();
		}
	}


	/**
	 * Notify Tyler whenever Evan posts something.
	 *
	 * @param author The author of the message
	 */
	private void interceptEvanPost(User author, GuildMessageChannel ch) {
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
	 * @param ch     The channel to which the message was sent
	 * @param lowercaseText    The lowercased content of the message
	 */
	private void interceptDavidMeme(User author, MessageChannel ch, String lowercaseText) {
		if (!author.isBot() && ch.getName().equals("david")) {
			if (lowercaseText.contains("david")) {
				ch.sendMessage("David").queue();
			}

			if (lowercaseText.contains("like")) {
				ch.sendMessage("I like moster trucks and David").queue();
			}

			if (lowercaseText.contains("coming") && author.getIdLong() == 104400026993709056L) {
				ch.sendMessage("David is coming").queue();
			}

			if (lowercaseText.contains("rick") || lowercaseText.contains("morty")) {
				ch.sendMessage("Rick & Morty").queue();
			}

			if (lowercaseText.contains("julia") || lowercaseText.contains("10th letter") || lowercaseText.contains("tenth letter")) {
				ch.sendMessage("Daily reminder").queue();
			}

			if (lowercaseText.contains("what") && lowercaseText.contains("is")) {
				ch.sendMessage("me").queue();
			}

			if (lowercaseText.contains("jeff")) {
				ch.sendMessage("Mah namma Jeeefffff").queue();
			}

			if (lowercaseText.contains("minion") || lowercaseText.contains("minin")) {
				ch.sendMessage("MINION").queue();
			}
		}
	}


	private void interceptWhoWouldaThoughtMeme(String msg, MessageChannel channel) {
		if (!Config.getConfig().interceptors().whoWouldaThought()) {
			return;
		}

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
				message.addReaction(Emoji.fromUnicode(THUMBS_DOWN)).queue(); // Thumbs down
				break;
			}
		}
	}


	private void interceptTwitterLink(Message message, String msg) {
		if (!Config.getConfig().interceptors().twitterEmbed()) {
			return;
		}

		String[] words = msg.split(" ");
		for (String word : words) {
			if (word.startsWith("https://x.com")) {
				String response = word.replace("x.com", "vxtwitter.com");
				message.reply(response).queue();
			}

			if (word.startsWith("https://twitter.com")) {
				String response = word.replace("twitter.com", "vxtwitter.com");
				message.reply(response).queue();
			}
		}
	}


	private void interceptMudaeBotRoll(Message message, String msg) {
		if (!Config.getConfig().interceptors().mudaeRolls()) {
			return;
		}

		if (!msg.startsWith("$")) {
			return;
		}

		String command = msg.split(" ")[0].toLowerCase();
		String replacement = switch (command) {
			case "$m"	-> "~smx";
			case "$mg"	-> "~smg";
			case "$ma"	-> "~sma";
			case "$h"	-> "~shx";
			case "$hg"	-> "~shg";
			case "$ha"	-> "~sha";
			case "$w"	-> "~swx";
			case "$wg"	-> "~swg";
			case "$wa"	-> "~swa";
			default -> null;
		};

		if (replacement == null) {
			return;
		}

		message.reply("https://api.memegen.link/images/drake/" + command + "/" + replacement + ".jpg").queue();
	}

}
