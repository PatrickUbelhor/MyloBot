package main;

import java.io.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

/**
 * @author PatrickUbelhor
 * @version 06/24/2017
 * @noinspection WeakerAccess
 */
public class Globals {
	
	private static final String CONFIG_PATH = "MyloBot.properties";
	
	public static final String TWITCH_CLIENT_ID;
	public static final String VOLUME;
	
	
	static {
		
		String tempTwitchClientId = "o0njrppa6gujdtnipdx8blw9pl6uvu";
		String tempVolume = "50";
		
		Properties prop = new Properties(){
			@Override
			public synchronized Enumeration<Object> keys() {
				return Collections.enumeration(new TreeSet<>(super.keySet()));
			}
		};
		
		
		File file = new File(CONFIG_PATH);
		OutputStream output = null;
		InputStream input = null;
		
		try {
			if (file.createNewFile()) {
				prop.setProperty("TwitchClientId", tempTwitchClientId);
				prop.setProperty("Volume", tempVolume);
				
				output = new FileOutputStream(file);
				prop.store(output, "Properties for MyloBot");
			} else {
				input = new FileInputStream(file);
				prop.load(input);
				
				tempTwitchClientId = prop.getProperty("TwitchClientId", tempTwitchClientId);
				tempVolume = prop.getProperty("Volume", tempVolume);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			TWITCH_CLIENT_ID = tempTwitchClientId;
			VOLUME = tempVolume;
		}
		
	}
	
	
}
