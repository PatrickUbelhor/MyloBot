package clients;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Patrick Ubelhor
 * @version 3/9/2021
 */
public class ShellClient {
	
	private static final Logger logger = LogManager.getLogger(ShellClient.class);

	public String getIp() {
		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			return getIpWindows();
		}
		
		// Otherwise, it's a unix-based system
		return getIpUnix();
	}
	
	
	private String getIpWindows() {
		String result = runWindowsCommand("nslookup myip.opendns.com. resolver1.opendns.com");
		String[] lines = result.split("\n");
		return lines[4].substring("Address:".length()).strip();
	}
	
	
	private String getIpUnix() {
		return runUnixCommand("dig TXT +short o-o.myaddr.l.google.com @ns1.google.com -4 | awk -F'\"' '{ print $2}'");
	}
	
	
	private String runWindowsCommand(String command) {
		return runGenericCommand("cmd.exe", "/c", command);
	}
	
	
	private String runUnixCommand(String command) {
		return runGenericCommand("sh", "-c", command);
	}
	
	
	private String runGenericCommand(String... command) {
		ProcessBuilder builder = new ProcessBuilder();
		builder.command(command);
		builder.directory(new File(System.getProperty("user.home")));
		
		// Start process
		Process process;
		try {
			process = builder.start();
		} catch (IOException e) {
			logger.error("Failed to start shell process");
			logger.error(e);
			return null;
		}
		
		// Read output
		StringBuilder result = new StringBuilder();
		InputStreamReader isr = new InputStreamReader(process.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		br.lines().forEachOrdered(s -> {
			result.append(s);
			result.append("\n");
		});
		
		// Close buffer
		try {
			br.close();
		} catch (IOException e) {
			logger.error("Failed to close BufferedReader");
			logger.error(e);
		}
		
		// Wait for process to terminate
		try {
			logger.debug("Waiting for shell to respond");
			int exitCode = process.waitFor();
			if (exitCode != 0) {
				logger.error("An error occurred while attempting to get IP. Exit status {}", exitCode);
			}
			
			logger.debug("Shell responded with '{}'", result.toString().strip());
			return result.toString();
		} catch (InterruptedException e) {
			logger.error("Failed while waiting for shell to exit");
			logger.error(e);
		}
		
		return null;
	}
	
}
