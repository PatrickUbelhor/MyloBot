package lib.commands;

import lib.main.Permission;
import main.Globals;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Patrick Ubelhor
 * @version 11/22/2023
 */
public abstract class Command {
	
	private static final Logger logger = LogManager.getLogger(Command.class);
	
	private final String name;
	private Permission perm;
	
	
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
		if (subInit()) {
			logger.info("\tInitialized command: !{}", this.getName());
			return true;
		}
		
		logger.error("\tFailed to initialize command: !{}", this.getName());
		return false;
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
			logger.warn("Failed to safely shut down command: {}", this.getName());
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
	
	
	public CommandData getCommandData() {
		String desc = "NOT YET IMPLEMENTED - " + getDescription();
		return new CommandData(this.getName(), desc.substring(0, Math.min(100, desc.length())));
	}
	
	
	protected CommandData getDefaultCommandData() {
		return this.getDefaultCommandData(getDescription());
	}
	
	
	protected CommandData getDefaultCommandData(String desc) {
		if (desc.length() > Globals.MAX_SLASH_COMMAND_DESC_LENGTH) {
			desc = desc.substring(0, Globals.MAX_SLASH_COMMAND_DESC_LENGTH);
			logger.error("Command '{}' description longer than {} characters: {}",
				this.getName(),
				Globals.MAX_SLASH_COMMAND_DESC_LENGTH,
				this.getDescription()
			);
		}
		
		return new CommandData(this.getName(), desc);
	}
	
	
	public void runSlash(SlashCommandInteractionEvent event) {
		event.reply("Not yet implemented").queue();
	}
	
	
	public abstract void run(MessageReceivedEvent event, String[] args);
	public abstract String getUsage();
	public abstract String getDescription();
//	public abstract CommandData getCommandData();
	
}
