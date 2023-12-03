package main;

import commands.Bug;
import commands.GetVoiceLog;
import commands.Help;
import commands.Random;
import commands.Reverse;
import commands.Roll;
import commands.Shutdown;
import commands.admin.AddRole;
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
import commands.party.Party;
import commands.party.Unparty;
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
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.SessionDisconnectEvent;
import net.dv8tion.jda.api.events.session.SessionRecreateEvent;
import net.dv8tion.jda.api.events.session.SessionResumeEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.IPChange;

import jakarta.annotation.Nonnull;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Patrick Ubelhor
 * @version 12/2/2023
 */
public class Bot extends ListenerAdapter {

	private static final Logger logger = LogManager.getLogger(Bot.class);
	private static final LinkedHashMap<String, Command> commands = new LinkedHashMap<>();
	private static final LinkedHashMap<String, Service> services = new LinkedHashMap<>();

	private static JDA jda;
	private static MessageInterceptor messageInterceptor;
	private static Lexer lexer;
	private static VoiceTrackerFileWriter tracker; // TODO: make sure this is a singleton so it gets closed
	private static VoiceTrackerTrigger voiceTrackerTrigger;
	private static PartyTrigger partyTrigger;
	private static List<Role> userRoles;
	private static List<Role> modRoles;

	public static void main(String[] args) {
		Config.load(Constants.CONFIG_PATH);

		// Create 'AtEveryone' directory if not found
		File pics = new File(Config.getConfig().AT_EVERYONE_PATH());
		if (!pics.exists() && !pics.mkdir()) {
			logger.error("Could not create 'AtEveryone' directory!");
		}


		// Log into Discord account
		try {
			jda = JDABuilder.createDefault(Config.getConfig().DISCORD_TOKEN())
				.enableIntents(
					GatewayIntent.GUILD_MEMBERS,
					GatewayIntent.MESSAGE_CONTENT
				)
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.setBulkDeleteSplittingEnabled(false)
				.setCompression(Compression.NONE)
				.build()
				.awaitReady();
		} catch (Exception e) {
			logger.fatal("Couldn't initialize bot", e);
			System.exit(2);
		}

		// Load
		List<Guild> guilds = jda.getGuilds();
		guilds.forEach(Guild::loadMembers);

		lexer = new Lexer(); // TODO: make a singleton
		messageInterceptor = new MessageInterceptor();
		partyTrigger = new PartyTrigger();
		voiceTrackerTrigger = new VoiceTrackerTrigger(jda);

		Command[] preInitCommands = {
			new Help(Permission.USER),
			new Play(Permission.USER),
			new PlayNext(Permission.USER),
			new Skip(Permission.USER),
			new Pause(Permission.USER),
			new Unpause(Permission.USER),
			new PeekQueue(Permission.USER),
			new Reverse(Permission.USER),
			new Random(Permission.USER),
			new Roll(Permission.USER),
			new Party(Permission.USER),
			new Unparty(Permission.USER),
			new WhoIs(Permission.USER),
			new Bug(Permission.USER),
			new ClearText(Permission.MOD),
			new AddRole(Permission.MOD),
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
			new IPChange(Config.getConfig().delay().IP()),
		};


		// Initialize triggers
		voiceTrackerTrigger.init();

		loadRoles();
		initializeCommands(preInitCommands);
		initializeServices(preInitServices);
		registerGuildSlashCommands(guilds, commands.values());

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


	private static void registerGuildSlashCommands(Collection<Guild> guilds, Collection<Command> commands) {
		logger.info("Registering guild slash commands...");
		List<SlashCommandData> commandData = commands.parallelStream()
			.map(Command::getCommandData)
			.toList();

		guilds.forEach(guild -> guild.updateCommands()
			.addCommands(commandData)
			.queue()
		);
	}


	private static void loadRoles() {
		logger.info("Getting roles...");
		// TODO: Check here if role actually exists?
		userRoles = Config.getConfig().groups().USER_GROUP_IDS()
			.parallelStream()
			.map(id -> jda.getRoleById(id))
			.collect(Collectors.toList());

		modRoles = Config.getConfig().groups().MOD_GROUP_IDS()
			.parallelStream()
			.map(id -> jda.getRoleById(id))
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


	public static VoiceTrackerTrigger getVoiceTrackerTrigger() {
		return voiceTrackerTrigger;
	}


	@Override
	public void onMessageReceived(MessageReceivedEvent event) {

		User author = event.getAuthor();
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();
		String msg = message.getContentDisplay().trim();

		messageInterceptor.intercept(event);


		// Tokenize and parse message
		List<Token> tokens = lexer.lex(msg);
		if (tokens.isEmpty() || tokens.get(0).type() != TokenType.COMMAND || author.isBot())
			return; // Checking isBot() prevents user from spamming a !reverse
		logger.info("Received: '" + msg + "'");

		for (Token token : tokens) {
			logger.debug(token.type().name() + " | " + token.data());
		}

		// If message ends with "&", then the message should be removed
		if (tokens.get(tokens.size() - 1).type() == TokenType.AMP) {
			message.delete().queue();
			tokens = tokens.subList(0, tokens.size() - 1);
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
			args[i] = tokens.get(i).data();
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
	public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
		super.onSlashCommandInteraction(event);
		String commandName = event.getName();
		Command command = commands.get(commandName);
		List<Role> authorRoles = event.getMember().getRoles();

		boolean isUser = userRoles.parallelStream()
			.anyMatch(authorRoles::contains);

		boolean isMod = modRoles.parallelStream()
			.anyMatch(authorRoles::contains);

		String response;
		User user = event.getUser();
		switch (command.getPerm()) {
			case DISABLED:
				response = String.format("``%s`` has been disabled by the bot admin, sorry!", commandName);
				user.openPrivateChannel().complete().sendMessage(response).queue();
				break;
			case USER:
				if (isUser || isMod) {
					command.runSlash(event);
				} else {
					response = String.format("You do not have permission to use ``%s``, sorry!", commandName);
					user.openPrivateChannel().complete().sendMessage(response).queue();
				}
				break;
			case MOD:
				if (isMod) {
					command.runSlash(event);
				} else {
					response = String.format("You do not have permission to use ``%s``, sorry!", commandName);
					user.openPrivateChannel().complete().sendMessage(response).queue();
				}
				break;
		}
	}


	@Override
	public void onSessionRecreate(@Nonnull SessionRecreateEvent event) {
		logger.info("Reconnected");
//		voiceTrackerTrigger.onReconnect();
	}


	@Override
	public void onReady(@Nonnull ReadyEvent event) {
		logger.info("Ready");
	}


	@Override
	public void onSessionResume(@Nonnull SessionResumeEvent event) {
		logger.info("Resumed");
	}


	@Override
	public void onSessionDisconnect(@Nonnull SessionDisconnectEvent event) {
		logger.info("Disconnected");
	}


	@Override
	public void onGuildVoiceUpdate(@Nonnull GuildVoiceUpdateEvent event) {
		super.onGuildVoiceUpdate(event);

		// If user joined
		if (event.getChannelLeft() == null) {
			partyTrigger.onGuildVoiceJoin(event);
			voiceTrackerTrigger.onGuildVoiceJoin(event);
			return;
		}

		// If user left
		if (event.getChannelJoined() == null) {
			partyTrigger.onGuildVoiceLeave(event);
			voiceTrackerTrigger.onGuildVoiceLeave(event);
			return;
		}

		// Else, user moved
		partyTrigger.onGuildVoiceMove(event);
		voiceTrackerTrigger.onGuildVoiceMove(event);
	}

}
