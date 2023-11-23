package commands;

import lib.commands.Command;
import main.Bot;
import lib.main.Permission;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Patrick Ubelhor
 * @version 10/16/2022
 */
public class Shutdown extends Command {
	
	private static final Logger logger = LogManager.getLogger(Shutdown.class);

	public Shutdown(Permission perm) {
		super("shutdown", perm);
	}

	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		shutdown(event.getJDA());
	}
	
	@Override
	public void runSlash(SlashCommandInteractionEvent event) {
		shutdown(event.getJDA());
	}
	
	private void shutdown(JDA jda) {
		logger.info("Shutting down...");
		killCommands();
		killServices();
		killTriggers();
		jda.shutdown();
		logger.info("Shutdown complete");
	}
	
	private void killCommands() {
		logger.info("Killing commands...");
		Bot.getCommands().values()
			.parallelStream()
			.forEach(command -> {
				command.end();
				logger.info("\tKilled {}", command.getName());
			});
		logger.info("All commands killed");
	}
	
	private void killServices() {
		logger.info("Killing services...");
		Bot.getServices().values()
			.parallelStream()
			.forEach(service -> {
				service.endThread();
				logger.info("\tKilled {}", service.getName());
			});
		logger.info("All services killed");
	}
	
	private void killTriggers() {
		logger.info("Killing VoiceTracker trigger...");
		Bot.getVoiceTrackerTrigger().end();
		logger.info("All triggers killed");
	}
	
	@Override
	public String getUsage() {
		return getName();
	}
	
	@Override
	public String getDescription() {
		return "Safely shuts down the bot";
	}
	
	public String getShortDescription() {
		return getDescription();
	}
	
	@Override
	public CommandData getCommandData() {
		return super.getDefaultCommandData();
	}
	
}
