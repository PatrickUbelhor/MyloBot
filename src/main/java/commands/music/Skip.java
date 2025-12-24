package commands.music;

import lib.commands.Command;
import lib.music.MusicManager;
import lib.music.TrackScheduler;
import lib.main.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * @author Patrick Ubelhor
 * @version 12/24/2025
 */
public final class Skip extends Command {
	
	public Skip(Permission permission) {
		super("skip", permission);
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		TrackScheduler trackScheduler = MusicManager.getInstance().getTrackScheduler(event.getGuild().getIdLong());
		MessageChannel channel = event.getChannel();
		
		if (args.length == 1) {
			channel.sendMessage("Skipping...").queue();
			trackScheduler.playNext();
			return;
		}
		
		String response = skip(trackScheduler, args[1]);
		channel.sendMessage(response).queue();
	}
	
	@Override
	public void runSlash(SlashCommandInteractionEvent event) {
		String quantity = "1";
		for (OptionMapping option : event.getOptionsByName("quantity")) {
			quantity = option.getAsString();
		}
		
		TrackScheduler trackScheduler = MusicManager.getInstance().getTrackScheduler(event.getGuild().getIdLong());
		String response = this.skip(trackScheduler, quantity);
		event.reply(response).queue();
	}
	
	private String skip(TrackScheduler scheduler, String quantity) {
		if (quantity.equals("all")) {
			scheduler.clearQueue();
			return "Clearing the queue";
		}
		
		try {
			int count = Integer.parseInt(quantity);
			
			if (count < 1) {
				return "Please give a positive integer";
			}
			
			scheduler.skip(count);
			return "Skipping...";
		} catch (NumberFormatException e) {
			return "Please enter a valid integer";
		}
	}
	
	@Override
	public String getUsage() {
		return getName() + " [all|number]";
	}
	
	@Override
	public String getDescription() {
		return "Skips the currently playing song";
	}
	
	public String getShortDescription() {
		return getDescription();
	}
	
	@Override
	public SlashCommandData getCommandData() {
		return super.getDefaultCommandData()
			.addOption(OptionType.STRING, "quantity", "Number of songs to skip", false);
	}
	
}
