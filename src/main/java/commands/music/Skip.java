package commands.music;

import lib.music.Music;
import lib.music.TrackScheduler;
import lib.main.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/**
 * @author Patrick Ubelhor
 * @version 10/16/2022
 */
public final class Skip extends Music {
	
	public Skip(Permission permission) {
		super("skip", permission);
	}
	
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		TrackScheduler trackScheduler = Music.trackSchedulers.get(event.getGuild().getIdLong());
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
	public void runSlash(SlashCommandEvent event) {
		String quantity = "1";
		for (OptionMapping option : event.getOptionsByName("quantity")) {
			quantity = option.getAsString();
		}
		
		TrackScheduler trackScheduler = Music.trackSchedulers.get(event.getGuild().getIdLong());
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
	public CommandData getCommandData() {
		return super.getDefaultCommandData()
			.addOption(OptionType.UNKNOWN, "quantity", "Number of songs to skip", false);
	}
	
}
