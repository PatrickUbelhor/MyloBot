package commands.music;

import lib.commands.Command;
import lib.main.Permission;
import lib.music.MusicManager;
import lib.music.TrackScheduler;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.SplitUtil;
import net.dv8tion.jda.api.utils.SplitUtil.Strategy;

import java.util.List;


/**
 * @author Patrick Ubelhor
 * @version 12/24/2025
 */
public class PeekQueue extends Command {

	public PeekQueue(Permission permission) {
		super("queue", permission);
	}

	@Override
	// TODO: add ability to view pages of queue
	public void run(MessageReceivedEvent event, String[] args) {
		TrackScheduler trackScheduler = MusicManager.getInstance().getTrackScheduler(event.getGuild().getIdLong());
		String playbackQueueMessage = generateQueueMessage(trackScheduler);
		List<String> splitMessages = splitMessages(playbackQueueMessage);

		MessageChannel channel = event.getChannel();
		splitMessages.forEach(msg -> channel.sendMessage(msg).queue());
	}

	@Override
	// TODO: add ability to view pages of queue
	public void runSlash(SlashCommandInteractionEvent event) {
		TrackScheduler trackScheduler = MusicManager.getInstance().getTrackScheduler(event.getGuild().getIdLong());
		String playbackQueueMessage = generateQueueMessage(trackScheduler);
		List<String> splitMessages = splitMessages(playbackQueueMessage);

		splitMessages.forEach(msg -> event.reply(msg).queue());
	}

	private List<String> splitMessages(String entireMessage) {
		return SplitUtil.split(
			entireMessage,
			2000,
			true,
			Strategy.NEWLINE, // split on '\n' characters if possible
			Strategy.ANYWHERE // otherwise split on the limit
		);
	}

	private String generateQueueMessage(TrackScheduler scheduler) {
		StringBuilder msg = new StringBuilder();
		List<String> titles = scheduler.getQueue();
		String currentSong = scheduler.getCurrentSong();

		if (currentSong == null) {
			return "No songs in queue!";
		}

		// Show current song
		msg.append("-> ");
		msg.append(currentSong);

		int i = 0;
		for (String title : titles) {
			msg.append("\n");
			msg.append(i);
			msg.append(". ");
			msg.append(title);
			i++;
		}

		return msg.toString();
	}

	@Override
	public String getUsage() {
		return getName();
	}

	@Override
	public String getDescription() {
		return "List the songs remaining in the queue";
	}

	@Override
	public SlashCommandData getCommandData() {
		return super.getDefaultCommandData();
	}

}
