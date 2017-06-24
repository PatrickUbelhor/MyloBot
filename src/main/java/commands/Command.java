package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.LinkedHashMap;

/**
 * @author PatrickUbelhor
 * @version 06/21/2017
 * @noinspection WeakerAccess
 */
public abstract class Command {
	
	private static final LinkedHashMap<String, Command> commandMap = new LinkedHashMap<>(12, 1f);
	
	protected Command() {
		commandMap.put(this.getName(), this);
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
			System.err.printf("\tFailed to initialize !%s\n", this.getName());
		} else {
			System.out.printf("\tInitialized !%s\n", this.getName());
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
			System.out.printf("Module %s failed to shut down properly!", this.getName());
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
		return this.getUsage().split(" ")[0];
	}
	
	
	public abstract void run(MessageReceivedEvent event, String[] args);
	public abstract String getUsage();
	public abstract String getDescription();
	
}
