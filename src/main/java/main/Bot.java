package main;

import commands.*;
import commands.music.*;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
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

import java.util.HashMap;
import java.util.Objects;

/**
 * @author PatrickUbelhor
 * @version 6/3/2017
 *
 * TODO: Optimize !clear to grab groups of messages when possible. Even if it's slow to check each individual one's age
 * and count them up, but it at least won't be choppy.
 */
public class Bot extends ListenerAdapter {
	
	private static final char KEY = '!';
	private static final HashMap<String, Command> commands = Command.getCommandMap();
	
	// Even though we don't use these variables, this still adds them to the HashMap
	private static final Help help = new Help();
	private static final CheckSurrender check = new CheckSurrender();
	private static final ClearText clearText = new ClearText();
	private static final Play play = new Play();
	private static final Skip skip = new Skip();
	private static final Pause pause = new Pause();
	private static final Unpause unpause = new Unpause();
	private static final AddPicture picture = new AddPicture();
	private static final Reverse reverse = new Reverse();
	private static final Shutdown shutdown = new Shutdown();
	
	private static JDA jda;

	public static void main(String[] args) {
		
		for (Command c : Command.getCommandList()) {
			c.init();
		}
		
		try {
			
			jda = new JDABuilder(AccountType.BOT)
							  .setToken("MjU1MTQ2Mzc5MDA4MDE2Mzk1.C_6mDw.kJCzmESndFbF17S0s6tuLdMGmmA")
//							  .setToken("MTkxMzI3NjQzNTc1MTIzOTY4.C2Kd8g.wXykM8CsgX6NIwD7GTnQp7DE-08") THIS IS REDBOT
							  .buildBlocking();
			jda.addEventListener(new Bot());
			
		} catch (Exception e) {
			e.printStackTrace();
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

		/*
		FIXME: Checking for '!' here makes David's autodelete code useless. Check afterwards to fix, but maybe not until
		we complete the 'TODO' below
		 */
		if (msg.charAt(0) != KEY || author.isBot()) return; // Checking isBot() prevents user from spamming a !reverse

		
		switch (event.getChannelType()) {
			case TEXT:
				// TODO: delete messages after a qualified period of time
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
		System.out.println("Reconnected");
	}
	
	
	@Override
	public void onReady(ReadyEvent event) {
		System.out.println("Ready");
	}
	
	
	@Override
	public void onResume(ResumedEvent event) {
		System.out.println("Resumed");
	}
	
	
	@Override
	public void onDisconnect(DisconnectEvent event) {
		System.out.println("Disconnected");
	}
	
}
