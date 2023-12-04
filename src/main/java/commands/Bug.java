package commands;

import lib.commands.Command;
import lib.main.Permission;
import main.Bot;
import main.Config;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * @author Patrick Ubelhor
 * @version 12/2/2023
 * @since 12/2/2023
 */
public class Bug extends Command {

	private static final Logger logger = LogManager.getLogger(Bug.class);

	public Bug(Permission permission) {
		super("bug", permission);
	}

	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();
		String info = joinString(Arrays.copyOfRange(args, 1, args.length));
		recordBug(event.getAuthor().getName(), info);
		channel.sendMessage("A defect report has been logged. Thank you.").queue();
	}

	@Override
	public void runSlash(SlashCommandInteractionEvent event) {
		String info = event.getOption("info", OptionMapping::getAsString);
		recordBug(event.getUser().getName(), info);
		event.reply("A defect report has been logged. Thank you.").queue();
	}

	private String joinString(String[] words) {
		StringBuilder msg = new StringBuilder();
		for (String word : words) {
			msg.append(" ");
			msg.append(word);
		}
		msg.deleteCharAt(0);

		return msg.toString();
	}

	private void recordBug(String username, String info) {
		logger.error("[BUG] Filed by: {} | Info: {}", username, info);
		Bot.getJDA()
			.getUserById(Config.getConfig().ADMIN_ID())
			.openPrivateChannel()
			.queue((PrivateChannel channel) -> {
				String msg = "Bug found by %s:\n%s".formatted(username, info);
				channel.sendMessage(msg).queue();
			});
	}

	@Override
	public String getUsage() {
		return getName();
	}

	@Override
	public String getDescription() {
		return "Files a bug report to help identify the issue.";
	}

	@Override
	public SlashCommandData getCommandData() {
		return super.getDefaultCommandData()
			.addOption(OptionType.STRING, "info", "Describe situation/actions that led to the error", false);
	}

}
