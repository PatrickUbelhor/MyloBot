package main;

import clients.VoiceTrackerClient;
import lib.triggers.Trigger;
import log.VoiceTrackerFileWriter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Patrick Ubelhor
 * @version 11/22/2023
 *
 * TODO: Could divide into a FileWriterTrigger and WebTrigger????
 * TODO: Add code to onReconnect in Bot.java to send proper events to all triggers
 */
public class VoiceTrackerTrigger implements Trigger {
	
	// Map of channelId -> set of userIds
	private HashMap<Long, HashSet<Long>> channels = new HashMap<>(); // TODO: Might need thread safety
	private final VoiceTrackerClient voiceTrackerClient = new VoiceTrackerClient();
	private VoiceTrackerFileWriter voiceTrackerFileWriter = null;
	private final JDA jda;
	private final Logger logger = LogManager.getLogger(VoiceTrackerTrigger.class);
	
	
	public VoiceTrackerTrigger(JDA jda) {
		this.jda = jda;
	}
	
	
	public void init() {
		try {
			this.voiceTrackerFileWriter = new VoiceTrackerFileWriter();
		} catch (IOException e) {
			logger.error("[Voice] Could not create VoiceTracker file writer!", e);
		}
		
		// Load data from file
		try (BufferedReader br = new BufferedReader(new FileReader("example.csv"))) {
			
			StringBuilder saveData = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				saveData.append(line).append("\n");
			}
			
			this.channels = loadData(saveData.toString()); // TODO: this should modify, rather than replace
			
		} catch (FileNotFoundException e) {
			logger.error("[Voice] Could not find save file", e);
		} catch (IOException e) {
			logger.error("[Voice] Could not read data from save file", e);
		}
	}
	
	
	public void end() {
		if (this.voiceTrackerFileWriter != null) {
			try {
				this.voiceTrackerFileWriter.close();
			} catch (IOException e) {
				logger.error("[Voice] Failed to close VoiceTracker file writer!", e);
			}
		}
		
		// Save data to file
		try (FileWriter fw = new FileWriter("example.csv")) {
			fw.append(this.saveData());
			fw.flush();
		} catch (IOException e) {
			logger.error("[Voice] Could not write data to save file");
		}
	}
	
	
	public void onGuildVoiceJoin(VoiceChannel channel, Member member) {
		logger.debug("[Voice] JOIN {} | {}", member.getEffectiveName(), channel.getName());
		
		handleJoin(channel, member);
		voiceTrackerClient.logJoinEvent(member.getIdLong(), channel.getIdLong());
		
		if (voiceTrackerFileWriter != null) {
			voiceTrackerFileWriter.enter(channel, member);
		}
	}
	
	
	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
		logger.debug("[Voice] JOIN {} | {}", event.getMember().getEffectiveName(), event.getChannelJoined().getName());
		
		VoiceChannel channelJoined = event.getChannelJoined();
		Member member = event.getMember();
		
		handleJoin(channelJoined, member);
		voiceTrackerClient.logJoinEvent(
				member.getIdLong(),
				channelJoined.getIdLong()
		);
		
		if (voiceTrackerFileWriter != null) {
			voiceTrackerFileWriter.enter(channelJoined, member);
		}
	}
	
	
	public void onGuildVoiceLeave(VoiceChannel channel, Member member) {
		logger.debug("[Voice] LEAVE {} | {}", member.getEffectiveName(), channel.getName());
		
		handleLeave(channel, member);
		voiceTrackerClient.logLeaveEvent(member.getIdLong(), channel.getIdLong());
		
		if (voiceTrackerFileWriter != null) {
			voiceTrackerFileWriter.exit(channel, member);
		}
	}
	
	
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		logger.debug("[Voice] LEAVE {} | {}", event.getMember().getEffectiveName(), event.getChannelLeft().getName());
		
		VoiceChannel channelLeft = event.getChannelLeft();
		Member member = event.getMember();
		
		handleLeave(channelLeft, member);
		voiceTrackerClient.logLeaveEvent(
				member.getIdLong(),
				channelLeft.getIdLong()
		);
		
		if (voiceTrackerFileWriter != null) {
			voiceTrackerFileWriter.exit(channelLeft, member);
		}
	}
	
	
	@Override
	public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
		logger.debug("[Voice] MOVE {} | {} -> {}",
				event.getMember().getEffectiveName(),
				event.getChannelLeft().getName(),
				event.getChannelJoined().getName()
		);
		
		VoiceChannel channelJoined = event.getChannelJoined();
		VoiceChannel channelLeft = event.getChannelLeft();
		Member member = event.getMember();
		
		handleLeave(channelLeft, member);
		handleJoin(channelJoined, member);
		voiceTrackerClient.logMoveEvent(
				member.getIdLong(),
				channelLeft.getIdLong(),
				channelJoined.getIdLong()
		);
		
		if (voiceTrackerFileWriter != null) {
			voiceTrackerFileWriter.move(event);
		}
	}
	
	
	public void onReconnect() {
		List<VoiceChannel> updatedChannels = new LinkedList<>();
		jda.getGuilds().forEach(guild -> updatedChannels.addAll(guild.getVoiceChannels()));
		
		for (VoiceChannel channel : updatedChannels) {
			HashSet<Long> activeUserIds = new HashSet<>(
					channel.getMembers()
							.stream()
							.map(ISnowflake::getIdLong)
							.toList()
			);
			
			if (channels.containsKey(channel.getIdLong())) {
				HashSet<Long> originalUsers = channels.get(channel.getIdLong());
				HashSet<Long> joinedUsers = new HashSet<>(activeUserIds);
				HashSet<Long> leftUsers = new HashSet<>(originalUsers);
				
				joinedUsers.removeAll(originalUsers);
				leftUsers.removeAll(activeUserIds);
				
				joinedUsers.forEach(userId -> voiceTrackerClient.logJoinEvent(userId, channel.getIdLong()));
				leftUsers.forEach(userId -> voiceTrackerClient.logLeaveEvent(userId, channel.getIdLong()));
				continue;
			}
			
			activeUserIds.forEach(userId -> voiceTrackerClient.logJoinEvent(userId, channel.getIdLong()));
			channels.put(channel.getIdLong(), activeUserIds);
		}
	}
	
	
	private void handleJoin(VoiceChannel vc, Member member) {
		Long channelId = vc.getIdLong();
		Long userId = member.getIdLong();
		
		channels.putIfAbsent(channelId, new HashSet<>());
		channels.get(channelId).add(userId);
	}
	
	
	private void handleLeave(VoiceChannel vc, Member member) {
		Long channelId = vc.getIdLong();
		Long userId = member.getIdLong();
		
		channels.put(channelId, new HashSet<>()); // Do we need this?
		channels.get(channelId).remove(userId);
	}
	
	
	private String saveData() {
		StringBuilder output = new StringBuilder();
		for (Long channelId : channels.keySet()) {
			// Don't log empty channels
			if (channels.get(channelId).isEmpty()) {
				continue;
			}
			
			output.append(channelId);
			for (Long userId : channels.get(channelId)) {
				output.append(',');
				output.append(userId);
			}
			
			output.append('\n');
		}
		
		return output.toString().stripTrailing();
	}
	
	
	private HashMap<Long, HashSet<Long>> loadData(String data) {
		HashMap<Long, HashSet<Long>> result = new HashMap<>();
		
		for (String line : data.split("\n")) {
			// First ID is channel ID. Rest are User IDs.
			String[] ids = line.split(",");
			HashSet<Long> userIds = new HashSet<>();
			for (int i = 1; i < ids.length; i++) {
				userIds.add(Long.parseUnsignedLong(ids[i]));
			}
			
			Long channelId = Long.parseUnsignedLong(ids[0]);
			result.put(channelId, userIds);
		}
		
		return result;
	}
	
}
