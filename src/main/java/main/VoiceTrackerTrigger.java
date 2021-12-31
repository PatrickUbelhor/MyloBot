package main;

import clients.VoiceTrackerClient;
import lib.triggers.Trigger;
import log.VoiceTrackerFileWriter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Patrick Ubelhor
 * @version 12/31/2021
 *
 * TODO: Could divide into a FileWriterTrigger and WebTrigger????
 * TODO: Add code to onReconnect in Bot.java to send proper events to all triggers
 */
public class VoiceTrackerTrigger implements Trigger {
	
	// Map of channelId -> set of userIds
	private final HashMap<Long, HashSet<Long>> channels = new HashMap<>(); // TODO: Might need thread safety
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
			logger.error("Couldn't create VoiceTracker file writer!", e);
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
}
