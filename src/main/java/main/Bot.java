package main;

import commands.AddPicture;
import commands.admin.Ban;
import commands.admin.ClearText;
import commands.Command;
import commands.Help;
import commands.Reverse;
import commands.Shutdown;
import commands.admin.Kick;
import commands.admin.Mute;
import commands.admin.Unmute;
import commands.admin.WhoIs;
import commands.music.Pause;
import commands.music.Play;
import commands.music.PlayNext;
import commands.music.Skip;
import commands.music.Unpause;
import commands.subscription.Subscribe;
import commands.subscription.Unsubscribe;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.ResumedEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static main.Globals.DISCORD_TOKEN;
import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 1/29/2019
 */
public class Bot extends ListenerAdapter {
	
	private static final LinkedHashMap<String, Command> commands = new LinkedHashMap<>();
	
	
	// Create 'AtEveryone' and 'Music' directories if not found
	static {
		File pics = new File("AtEveryone");
		if (!pics.exists() && !pics.mkdir()) {
			logger.error("Could not create 'AtEveryone' directory!");
		}
		
		File music = new File("Music");
		if (!music.exists() && !music.mkdir()) {
			logger.error("Could not create 'Music' directory!");
		}
	}
	
	
	private static JDA jda;
	private static Lexer lexer;
	private static List<Role> userRoles;
	private static List<Role> modRoles;
	
	public static void main(String[] args) {
		
		try {
			// Log into Discord account
			jda = new JDABuilder(AccountType.BOT)
					.setToken(DISCORD_TOKEN)
					.build()
					.awaitReady();
			
			// Initialize lexer
			lexer = new Lexer(); // TODO: make a singleton
			
			
			// Instantiate commands
			Command[] preInitCommands = {
					new Help(),
					new AddPicture(Permission.DISABLED),
					new Play(),
					new PlayNext(),
					new Skip(),
					new Pause(),
					new Unpause(),
					new commands.Random(), // TODO: fix naming collision
					new Reverse(),
					new ClearText(Permission.MOD),
					new WhoIs(Permission.USER),
					new Kick(Permission.MOD),
					new Ban(Permission.MOD),
					new Mute(Permission.MOD),
					new Unmute(Permission.MOD),
					new Subscribe(Permission.MOD),
					new Unsubscribe(Permission.MOD),
					new Shutdown(Permission.MOD)
			};
			
			
			// Initialize commands
			logger.info("Initializing commands...");
			Arrays.stream(preInitCommands)
					.parallel()
					.filter(Command::init)
					.forEachOrdered(command -> commands.put(command.getName(), command));
			logger.info("Initialization finished.");
			
			
			// Get Role object for 'user' and 'mod' (defined in config)
			logger.info("Getting roles...");
			String[] userRoleIds = Globals.USER_GROUP_IDS.split(",");
			userRoles = new LinkedList<>();
			for (String s : userRoleIds) {
				userRoles.add(jda.getRoleById(s));
			}
			
			String[] modRoleIds = Globals.MOD_GROUP_IDS.split(",");
			modRoles = new LinkedList<>();
			for (String s : modRoleIds) {
				modRoles.add(jda.getRoleById(s));
			}
			logger.info("Got roles.");
			
			jda.addEventListener(new Bot());
			
		} catch (Exception e) {
			logger.fatal("Couldn't initialize bot", e);
		}
		
	}
	
	
	public static JDA getJDA() {
		return jda;
	}
	
	
	/**
	 * @return A HashMap containing all active commands, referenced by their first required argument
	 */
	public static LinkedHashMap<String, Command> getCommands() {
		return commands;
	}
	
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		
		User author = event.getAuthor();
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();
		TextChannel ch = event.getTextChannel();
		String msg = message.getContentDisplay().trim();
		
		// Post @everyone meme
		if (message.mentionsEveryone()) {
			File[] pics = new File("AtEveryone").listFiles();
			
			if (pics == null || pics.length == 0) {
				channel.sendMessage("reeeeEEEEEEEEEEEE E E E E E E E E E E E E E").queue();
				return;
			}
			
			channel.sendFile(pics[new Random().nextInt(pics.length)]).queue();
			
			return;
		}
		
		// Send "David" to the 'david' thread when prompted
		if (!author.isBot() && ch.getName().equals("david")) {
			if (msg.toLowerCase().contains("david")) {
				ch.sendMessage("David").queue();
			}
			
			if (msg.toLowerCase().contains("like")) {
				ch.sendMessage("I like monster trucks and David").queue();
			}
		}
		
		
		List<Token> tokens = lexer.lex(msg);
		if (tokens.isEmpty() || tokens.get(0).getType() != TokenType.COMMAND || author.isBot())
			return; // Checking isBot() prevents user from spamming a !reverse
		logger.info("Received: '" + msg + "'");
		
		for (Token token : tokens) {
			logger.debug(token.getType().name() + " | " + token.getData());
		}
		
		
		switch (event.getChannelType()) {
			case TEXT:
				// Do nothing here (for now)
				break;
			case PRIVATE:
				// If from a DM, do special stuff here
				return;
			case GROUP:
				// If from a group message, do special stuff here
				return;
		}
		
		String[] args = new String[tokens.size()];
		for (int i = 0; i < args.length; i++) {
			args[i] = tokens.get(i).getData();
		}
		args[0] = args[0].substring(1).toLowerCase();
		
		// Runs the command, if it exists and the user has valid permission levels. Otherwise prints an error message
		if (commands.containsKey(args[0])) {
			Command command = commands.get(args[0]);
			List<Role> authorRoles = event.getMember().getRoles();
			
			boolean isUser = userRoles.parallelStream()
					.anyMatch(authorRoles::contains);
			
			boolean isMod = modRoles.parallelStream()
					.anyMatch(authorRoles::contains);
			
			
			// Call the command, given the user has proper permissions
			String response;
			switch (command.getPerm()) {
				case DISABLED:
					response = String.format("``%s`` has been disabled by the bot admin, sorry!", args[0]);
					author.openPrivateChannel().complete().sendMessage(response).queue();
					break;
				case USER:
					if (isUser || isMod) {
						command.run(event, args);
					} else {
						response = String.format("You do not have permission to use ``%s``, sorry!", args[0]);
						author.openPrivateChannel().complete().sendMessage(response).queue();
					}
					break;
				case MOD:
					if (isMod) {
						command.run(event, args);
					} else {
						response = String.format("You do not have permission to use ``%s``, sorry!", args[0]);
						author.openPrivateChannel().complete().sendMessage(response).queue();
					}
					break;
			}
			
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
