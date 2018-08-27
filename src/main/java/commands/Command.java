package commands;

import main.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 8/26/2018
 * @noinspection WeakerAccess
 */
public abstract class Command {
	
	private final String name;
	private Permission perm;
	
	protected Command(String name) {
		this(name, Permission.USER);
	}

	
	protected Command(String name, Permission perm) {
		this.name = name.toLowerCase();
		this.perm = perm;
	}
	
	
	/**
	 * Runs any initialization code that the command needs before being called,
	 * like creating or counting files. Removes the command from data the HashMap
	 * and ArrayList if initialization fails.
	 */
	public final boolean init() {
		if (!subInit()) {
			logger.error(String.format("\tFailed to initialize !%s", this.getName()));
			return false;
		}
		
		logger.info(String.format("\tInitialized !%s", this.getName()));
		return true;
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
	 * For example, the name in "!clear 100" is "clear".
	 *
	 * @return The command name
	 */
	public final String getName() {
		return name;
	}
	
	
	/**
	 * @return The permission level required to call this command.
	 */
	public final Permission getPerm() {
		return perm;
	}


	/**
	 * Sets the permission level required to call this command.
	 *
	 * @param perm The permission level
	 */
	public final void setPerm(Permission perm) {
		this.perm = perm;
	}
	
	
	public abstract void run(MessageReceivedEvent event, String[] args);
	public abstract String getUsage();
	public abstract String getDescription();
	
}
