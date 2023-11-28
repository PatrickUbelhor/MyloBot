package commands.admin;

import lib.commands.Command;
import lib.main.Permission;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Patrick Ubelhor
 * @version 11/27/2023
 */
public class ClearText extends Command {

	private static final Logger logger = LogManager.getLogger(ClearText.class);
	private static final int MAX_MESSAGE_COUNT = 100; // JDA throws exception after this point

	public ClearText(Permission perm) {
		super("clear", perm);
	}

	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		MessageChannel channel = event.getChannel();

		if (args.length < 2) {
			event.getChannel().sendMessage("Usage: " + getUsage()).queue();
			return;
		}

		int num;
		try {
			num = Integer.parseInt(args[1]) + 1; // Plus one to delete the command itself too
		} catch (NumberFormatException e) {
			channel.sendMessage("I think you specified an invalid number of messages to delete: '" + args[1] + "'").queue();
			return;
		}

		deleteMessages(num, channel);
	}

	@Override
	public void runSlash(SlashCommandInteractionEvent event) {
		event.reply("Deleting messages...")
			.setEphemeral(true)
			.queue();

		int num = event.getOption("num").getAsInt();

		deleteMessages(num, event.getChannel());
		event.reply("Finished deleting messages")
			.setEphemeral(true)
			.queue();
	}

	private void deleteMessages(int num, MessageChannel channel) {
		logger.info("Retrieving and deleting message history...");
		MessageHistory messageHistory = new MessageHistory(channel);

		// Retrieve the list of messages to delete
		for (int i = 0; i < num / MAX_MESSAGE_COUNT; i++) {
			messageHistory.retrievePast(MAX_MESSAGE_COUNT).complete();
		}
		messageHistory.retrievePast(num % MAX_MESSAGE_COUNT).complete();

		channel.purgeMessages(messageHistory.getRetrievedHistory());
		logger.info("Message history deleted.");
	}

	@Override
	public String getUsage() {
		return getName() + " <num>";
	}

	@Override
	public String getDescription() {
		return "Deletes 'num' amount of messages from the chat";
	}

	public String getShortDescription() {
		return "Deletes a collection of messages from the chat";
	}

	@Override
	public SlashCommandData getCommandData() {
		return super.getDefaultCommandData(getShortDescription())
			.addOptions(
				new OptionData(OptionType.INTEGER, "num", "The number of messages to delete", true)
					.setMinValue(1)
					.setMaxValue(Integer.MAX_VALUE)
			);
	}

}
