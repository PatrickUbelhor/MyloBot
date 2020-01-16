package commands.admin;

import commands.Command;
import main.Globals;
import main.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * @author Patrick Ubelhor
 * @version 1/16/2020
 */
public class GetIp extends Command {
	
	public GetIp(Permission perm) {
		super("getIP", perm);
	}
	
	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		TextChannel channel = event.getTextChannel();
		
		// Don't want to deal with CMD, so just fail if running on Windows
		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			channel.sendMessage("I don't know how to do this on Windows").queue();
			return;
		}
		
		ProcessBuilder builder = new ProcessBuilder();
		builder.command("sh -c wget http://checkip.dyndns.org/ -O - -o /dev/null | cut -d: -f 2 | cut -d < -f 1".split(" "));
		builder.directory(new File(System.getProperty("user.home")));
		try {
			Process process = builder.start();
			StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), s -> channel.sendMessage(s).queue());
			Executors.newSingleThreadExecutor().submit(streamGobbler);
			
			int exitCode = process.waitFor();
			if (exitCode != 0) {
				Globals.logger.error("An error occurred while attempting to get IP. Exit status " + exitCode);
			}
		} catch (IOException | InterruptedException e) {
			Globals.logger.error("Failed to get public IP");
			Globals.logger.error(e);
			channel.sendMessage("An error occurred when trying to get my IP").queue();
		}
	}
	
	
	@Override
	public String getUsage() {
		return getName();
	}
	
	
	@Override
	public String getDescription() {
		return "Gets the IP of the server";
	}
	
	
	private static class StreamGobbler implements Runnable {
		private InputStream inputStream;
		private Consumer<String> consumer;
		
		public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
			this.inputStream = inputStream;
			this.consumer = consumer;
		}
		
		@Override
		public void run() {
			new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
		}
	}
	
}
