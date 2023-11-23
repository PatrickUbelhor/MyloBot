package commands.music;

import lib.music.Music;
import lib.music.TrackScheduler;
import lib.main.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/**
 * @author Patrick Ubelhor
 * @version 10/16/2022
 */
public class Disconnect extends Music {
	
	public Disconnect(Permission permission) {
		super("disconnect", permission);
	}
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		if (super.leaveAudioChannel(event)) {
			TrackScheduler trackScheduler = Music.trackSchedulers.get(event.getGuild().getIdLong());
			trackScheduler.clearQueue();
		}
	}
	
	@Override
	public void runSlash(SlashCommandInteractionEvent event) {
		event.reply("Leaving call").queue();
		if (super.leaveAudioChannel(event)) {
			TrackScheduler trackScheduler = Music.trackSchedulers.get(event.getGuild().getIdLong());
			trackScheduler.clearQueue();
		}
	}
	
	@Override
	public String getUsage() {
		return getName();
	}
	
	@Override
	public String getDescription() {
		return "Disconnects from the voice chat";
	}
	
	@Override
	public CommandData getCommandData() {
		return super.getDefaultCommandData();
	}
	
}
