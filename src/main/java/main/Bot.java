package main;

import commands.GetVoiceLog;
import commands.Help;
import commands.Party;
import commands.Random;
import commands.Reverse;
import commands.Roll;
import commands.Shutdown;
import commands.admin.Ban;
import commands.admin.ClearText;
import commands.admin.GetIp;
import commands.admin.Kick;
import commands.admin.Mute;
import commands.admin.Unmute;
import commands.admin.WhoIs;
import commands.music.Disconnect;
import commands.music.Pause;
import commands.music.PeekQueue;
import commands.music.Play;
import commands.music.PlayNext;
import commands.music.Skip;
import commands.music.Unpause;
import commands.subscription.Subscribe;
import commands.subscription.Unsubscribe;
import lib.commands.Command;
import lib.main.Lexer;
import lib.main.Permission;
import lib.main.Token;
import lib.main.TokenType;
import lib.services.Service;
import log.VoiceTrackerFileWriter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.ResumedEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.IPChange;
import services.SurrenderAt20;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static main.Globals.DISCORD_TOKEN;

/**
 * @author Patrick Ubelhor
 * @version 5/16/2021
 *
 * TODO: make a simple setStatus method for setting the bot's Discord status?
 */
public class Bot extends ListenerAdapter {
	
	private static final Logger logger = LogManager.getLogger(Bot.class);
	private static final LinkedHashMap<String, Command> commands = new LinkedHashMap<>();
	private static final LinkedHashMap<String, Service> services = new LinkedHashMap<>();
	private static final Party partyCommand = new Party(Permission.USER);
	
	private static JDA jda;
	private static MessageInterceptor messageInterceptor;
	private static Lexer lexer;
	private static VoiceTrackerFileWriter tracker; // TODO: make sure this is a singleton so it gets closed
	private static VoiceTrackerTrigger voiceTrackerTrigger;
	private static List<Role> userRoles;
	private static List<Role> modRoles;
	
	public static void main(String[] args) {
		
		// Create 'AtEveryone' directory if not found
		File pics = new File(Globals.AT_EVERYONE_PATH);
		if (!pics.exists() && !pics.mkdir()) {
			logger.error("Could not create 'AtEveryone' directory!");
		}
		
		
		// Log into Discord account
		try {
			jda = JDABuilder.createDefault(DISCORD_TOKEN)
					.enableIntents(GatewayIntent.GUILD_MEMBERS)
					.setMemberCachePolicy(MemberCachePolicy.ALL)
					.build()
					.awaitReady();
		} catch (Exception e) {
			logger.fatal("Couldn't initialize bot", e);
			System.exit(2);
		}
		
		messageInterceptor = new MessageInterceptor();
		lexer = new Lexer(); // TODO: make a singleton
		voiceTrackerTrigger = new VoiceTrackerTrigger(jda);
		
		Command[] preInitCommands = {
				new Help(),
				new Play(),
				new PlayNext(),
				new Skip(),
				new Pause(),
				new Unpause(),
				new PeekQueue(),
				new Reverse(),
				new Random(),
				new Roll(Permission.USER),
				partyCommand,
				new ClearText(Permission.MOD),
				new WhoIs(Permission.USER),
				new Kick(Permission.MOD),
				new Ban(Permission.MOD),
				new Mute(Permission.MOD),
				new Unmute(Permission.MOD),
				new Subscribe(Permission.MOD),
				new Unsubscribe(Permission.MOD),
				new Disconnect(Permission.MOD),
				new Shutdown(Permission.MOD),
				new GetIp(Permission.MOD),
				new GetVoiceLog(Permission.MOD, tracker)
		};
		
		Service[] preInitServices = {
				new IPChange(Globals.IP_CHECK_DELAY),
				new SurrenderAt20(Globals.SURRENDER_DELAY)
		};
		
		
		// Initialize triggers
		voiceTrackerTrigger.init();
		
		initializeCommands(preInitCommands);
		initializeServices(preInitServices);
		
		loadRoles();
		
		// Load
		jda.getGuilds().forEach(Guild::loadMembers);
		jda.addEventListener(new Bot());
	}
	
	
	private static void initializeCommands(Command[] preInitCommands) {
		logger.info("Initializing commands...");
		Arrays.stream(preInitCommands)
				.parallel()
				.filter(Command::init)
				.forEachOrdered(command -> commands.put(command.getName(), command));
		logger.info("Initialization finished");
	}
	
	
	private static void initializeServices(Service[] preInitServices) {
		logger.info("Initializing services...");
		Service.loadSubscribers();
		Arrays.stream(preInitServices)
				.forEachOrdered(service -> {
					service.startThread();
					services.put(service.getName(), service);
				});
		logger.info("Initialization finished");
	}
	
	
	private static void loadRoles() {
		logger.info("Getting roles...");
		// TODO: Check here if role actually exists?
		userRoles = Globals.USER_GROUP_IDS
				.parallelStream()
				.map(s -> jda.getRoleById(s))
				.collect(Collectors.toList());
		
		modRoles = Globals.MOD_GROUP_IDS
				.parallelStream()
				.map(s -> jda.getRoleById(s))
				.collect(Collectors.toList());
		logger.info("Got roles");
	}
	
	
	public static JDA getJDA() {
		return jda;
	}
	
	
	public static void setStatusMessage(Activity activity) {
		jda.getPresence().setActivity(activity);
	}
	
	
	/**
	 * @return A HashMap containing all active commands, referenced by their first required argument
	 */
	public static LinkedHashMap<String, Command> getCommands() {
		return commands;
	}
	
	
	/**
	 * @return A list of all services that were started
	 */
	public static LinkedHashMap<String, Service> getServices() {
		return services;
	}
	
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		
		User author = event.getAuthor();
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();
		TextChannel ch = event.getTextChannel();
		String msg = message.getContentDisplay().trim();
		
		messageInterceptor.intercept(event);
		
		
		// Tokenize and parse message
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
	public void onReconnected(@Nonnull ReconnectedEvent event) {
		logger.info("Reconnected");
//		voiceTrackerTrigger.onReconnect();
	}
	
	
	@Override
	public void onReady(@Nonnull ReadyEvent event) {
		logger.info("Ready");
	}
	
	
	@Override
	public void onResumed(@Nonnull ResumedEvent event) {
		logger.info("Resumed");
	}
	
	
	@Override
	public void onDisconnect(@Nonnull DisconnectEvent event) {
		logger.info("Disconnected");
	}
	
	
	@Override
	public void onUserUpdateOnlineStatus(@Nonnull UserUpdateOnlineStatusEvent event) {}
	
	
	@Override
	public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
		partyCommand.onGuildVoiceJoin(event);
		voiceTrackerTrigger.onGuildVoiceJoin(event);
	}
	
	
	@Override
	public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
		partyCommand.onGuildVoiceLeave(event);
		voiceTrackerTrigger.onGuildVoiceLeave(event);
	}
	
	
	@Override
	public void onGuildVoiceMove(@Nonnull GuildVoiceMoveEvent event) {
		partyCommand.onGuildVoiceMove(event);
		voiceTrackerTrigger.onGuildVoiceMove(event);
	}
	
}
