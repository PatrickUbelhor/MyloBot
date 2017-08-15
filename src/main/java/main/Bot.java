package main;

import commands.*;
import commands.music.*;
import commands.subscription.Subscribe;
import commands.subscription.Unsubscribe;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.ResumedEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Random;

import static main.Globals.DISCORD_TOKEN;
import static main.Globals.logger;

/**
 * @author PatrickUbelhor
 * @version 6/30/2017
 * TODO: On Twitch startup, verify token is valid
 */
public class Bot extends ListenerAdapter {
	
	private static final char KEY = '!';
	private static final LinkedHashMap<String, Command> commands = Command.getCommandMap();
	
	// Even though we don't use these variables, this still adds them to the HashMap
	private static final Help help = new Help();
	private static final ClearText clearText = new ClearText();
	private static final Play play = new Play();
	private static final Skip skip = new Skip();
	private static final Pause pause = new Pause();
	private static final Unpause unpause = new Unpause();
	private static final AddPicture picture = new AddPicture();
	private static final Reverse reverse = new Reverse();
	private static final Shutdown shutdown = new Shutdown();
	private static final Subscribe sub = new Subscribe();
	private static final Unsubscribe unsub = new Unsubscribe();
	
	private static final String[] images = {
		// TODO: move these to folder "AtEveryone". Search the folder for images.
			"https://i.imgur.com/gjRp51B.gif",
			"http://i3.kym-cdn.com/photos/images/original/001/242/548/f0f.jpg",
	        "http://i2.kym-cdn.com/photos/images/original/001/243/406/73c.jpg",
	        "https://i.ytimg.com/vi/pAcf_VV8KmI/maxresdefault.jpg",
	        "https://i.redditmedia.com/xpQhnmEXXTecqj4sItzLz3KcCnnX-U64lZm_fo4-gF0.png?w=320&s=2348e46110cc92a1c32be25c2bf69c5d"
	};
	
	private static JDA jda;

	public static void main(String[] args) {
		
		try {
			
			jda = new JDABuilder(AccountType.BOT)
				      .setToken(DISCORD_TOKEN)
				      .buildBlocking();
			
			logger.info("Initializing commands...");
			for (Command c : Command.getCommandMap().values().toArray(new Command[] {})) {
				c.init();
			}
			logger.info("Initialization finished.");
			
			jda.addEventListener(new Bot());
			
		} catch (Exception e) {
			logger.error("Couldn't initialize bot", e);
		}
		
	}
	
	
	public static JDA getJDA() {
		return jda;
	}

	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		
		User author = event.getAuthor();
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();
		TextChannel ch = event.getTextChannel();
		String msg = message.getContent().trim();
		
		// TODO: could possibly make this a subscription service?
		if (message.mentionsEveryone()) {
			// Post atEveryone meme
			String image = images[new Random().nextInt(images.length)];
			
			MessageBuilder mb = new MessageBuilder().setEmbed(new EmbedBuilder().setImage(image).build());
			Message m = mb.build();
			channel.sendMessage(m).queue();
			return;
		}
		
		
		/*
		FIXME: Checking for '!' here makes David's autodelete code useless. Check afterwards to fix, but maybe not until
		we complete the 'TODO' below
		 */
		if ((msg.length() > 0 && msg.charAt(0) != KEY) || author.isBot()) return; // Checking isBot() prevents user from spamming a !reverse

		
		switch (event.getChannelType()) {
			case TEXT:
				// TODO: delete messages after a qualified period of time
				// TODO: make autodelete a subscription service
				if (Objects.equals(ch.getName(), "patricks_taxes") ||
				    Objects.equals(ch.getName(), "twitch_streams")) {
					
					message.delete().queue();
				}
				
				break;
			case PRIVATE:
				// If from a DM, do special stuff here
				return;
			case GROUP:
				// If from a group message, do special stuff here
				return;
		}
		
		String[] args = msg.substring(1).split(" ");
		args[0] = args[0].toLowerCase();
		
		// Runs the command, if it exists. Otherwise prints an error message
		if (commands.containsKey(args[0])) {
			commands.get(args[0]).run(event, args);
		} else {
			channel.sendMessage("Unknown or unavailable command").queue();
		}
		
	}
	
	
	@Override
	public void onReconnect(ReconnectedEvent event) {
		logger.info("Reconnected");
	}
	
	
	@Override
	public void onReady(ReadyEvent event) {
		logger.info("Ready");
	}
	
	
	@Override
	public void onResume(ResumedEvent event) {
		logger.info("Resumed");
	}
	
	
	@Override
	public void onDisconnect(DisconnectEvent event) {
		logger.info("Disconnected");
	}
	
}
