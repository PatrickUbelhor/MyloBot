package commands.music;

import lib.music.Music;
import lib.music.QueueLastAudioLoadResultHandler;
import lib.music.TrackScheduler;
import lib.main.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author Patrick Ubelhor
 * @version 10/16/2022
 *
 * TODO: Make bot leave voice channel after some period of inactivity
 * TODO: Ability to loop
 */
public final class Play extends Music {
	
	
	public Play(Permission permission) {
		super("play", permission);
	}
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		
		if (args.length < 2) return;
		
		// Joins the voice channel if not in one
		if (!joinAudioChannel(event.getGuild(), event.getMember())) {
			event.getChannel().sendMessage("You must be in a voice channel to begin playing music.").queue();
			return; // If we failed to join a voice channel, return
		}
		
		// Directly add song to queue and return if it's a link
		if (args[1].startsWith("http") || args[1].startsWith("www")) {
			TrackScheduler trackScheduler = Music.trackSchedulers.get(event.getGuild().getIdLong());
			playerManager.loadItem(args[1], new QueueLastAudioLoadResultHandler(trackScheduler));
			return;
		}
		
		// Build YouTube query
		StringBuilder queryBuilder = new StringBuilder();
		for (int i = 1; i < args.length; i++) {
			queryBuilder.append(args[i]);
			queryBuilder.append(' ');
		}
		String query = queryBuilder.toString().strip();
		
		// TODO: send YouTube query
		// TODO: Fix PlayNext
				
	}
	
	@Override
	public void runSlash(SlashCommandInteractionEvent event) {
		// Joins the voice channel if not in one
		if (!joinAudioChannel(event.getGuild(), event.getMember())) {
			event.reply("You must be in a voice channel to begin playing music.").queue();
			return; // If we failed to join a voice channel, return
		}
		
		String link = "";
		for (OptionMapping option : event.getOptionsByName("link")) {
			link = option.getAsString();
		}
		
		if (!link.startsWith("http")) {
			event.reply("Song must be given as a webpage link").queue();
			return;
		}
		
		TrackScheduler trackScheduler = Music.trackSchedulers.get(event.getGuild().getIdLong());
		playerManager.loadItem(link, new QueueLastAudioLoadResultHandler(trackScheduler));
		event.reply("Adding song to queue: " + link).queue();
	}
	
	@Override
	public String getUsage() {
		return getName() + " <url>";
	}
	
	@Override
	public String getDescription() {
		return "Plays the audio from a YouTube video, given as 'url'";
	}
	
	public String getShortDescription() {
		return "Plays the audio from a YouTube video, or appends to queue";
	}
	
	@Override
	public CommandData getCommandData() {
		return super.getDefaultCommandData(getShortDescription())
			.addOptions(
				new OptionData(OptionType.STRING, "link", "URL of a song", true)
			);
	}
	
}
