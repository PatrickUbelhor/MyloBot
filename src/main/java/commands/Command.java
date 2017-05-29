package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author PatrickUbelhor
 * @version 5/29/2017
 */
public abstract class Command {
	
	private static ArrayList<Command> commandList = new ArrayList<>(12); // Used to manually order the commands for 'help'
	private static HashMap<String, Command> commandMap = new HashMap<>(12, 1f);
	
	protected Command() {
		commandList.add(this);
		commandMap.put(this.getName(), this);
	}
	
	
	/**
	 * @return An ArrayList containing all active commands
	 */
	public static ArrayList<Command> getCommandList() {
		return commandList;
	}
	
	
	/**
	 * @return A HashMap containing all active commands, referenced by their first required argument
	 */
	public static HashMap<String, Command> getCommandMap() {
		return commandMap;
	}
	
	
	/**
	 * Runs any initialization code that the command needs before being called,
	 * like creating or counting files. Removes the command from data the HashMap
	 * and ArrayList if initialization fails.
	 */
	public final void init() {
		if (!subInit()) {
			commandList.remove(this);
			commandMap.remove(this.getName());
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
