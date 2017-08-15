package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.LinkedHashMap;

import static main.Globals.logger;

/**
 * @author PatrickUbelhor
 * @version 8/15/2017
 * @noinspection WeakerAccess
 */
public abstract class Command {
	
	private static final LinkedHashMap<String, Command> commandMap = new LinkedHashMap<>(12, 1f);
	private final String name;
	
	protected Command(String name) {
		this.name = name.toLowerCase();
		commandMap.put(name, this);
	}
	
	
	/**
	 * @return A HashMap containing all active commands, referenced by their first required argument
	 */
	public static LinkedHashMap<String, Command> getCommandMap() {
		return commandMap;
	}
	
	
	/**
	 * Runs any initialization code that the command needs before being called,
	 * like creating or counting files. Removes the command from data the HashMap
	 * and ArrayList if initialization fails.
	 */
	public final void init() {
		if (!subInit()) {
			commandMap.remove(this.getName());
			logger.error(String.format("\tFailed to initialize !%s", this.getName()));
		} else {
			logger.info(String.format("\tInitialized !%s", this.getName()));
		}
	}
	
	
	/**
	 * Runs any initialization code that the command needs before being called,
	 * like creating or counting files.
	 *
	 * @return True if initialization succeeded, otherwise false
	 */
	protected boolean subInit() {
		return true;
	}
	
	
	/**
	 * Runs finalization code that runs when the bot shuts down. Sends failure
	 * message to console if an error occurs when closing a command.
	 */
	public final void end() {
		if (!subEnd()) {
			logger.error(String.format("Module %s failed to shut down properly!", this.getName()));
		}
	}
	
	
	/**
	 * Runs finalization code that runs when the bot shuts down.
	 *
	 * @return True if finalization succeeded, otherwise false
	 */
	protected boolean subEnd() {
		return true;
	}
	
	
	/**
	 * Gets the String the user calls (minus the prefix) to invoke this command.
	 * For example, the name in "!clear 100" is "clear"
	 *
	 * @return The command name
	 */
	public String getName() {
		return name;
	}
	
	
	public abstract void run(MessageReceivedEvent event, String[] args);
	public abstract String getUsage();
	public abstract String getDescription();
	
}
