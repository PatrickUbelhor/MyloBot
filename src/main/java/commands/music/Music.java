package commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import commands.Command;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import main.Globals;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 5/1/2018
 *
 * TODO: Add responses to user interaction
 * TODO: Only add files with given file extensions
 * TODO: Play albums and playlists in order
 */
abstract class Music extends Command {
	
	static AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	static AudioPlayer player = playerManager.createPlayer();
	static TrackScheduler trackScheduler = new TrackScheduler(player);
	protected static File musicFolder = new File("music"); // FIXME: magic string is bad. Make a global variable
	protected static HashMap<String, String> songs = new HashMap<>();
	protected static LinkedHashMap<String, LinkedList<String>> albums = new LinkedHashMap<>();
	private static boolean hasInit = false;
	
	protected Music(String name) {
		super(name);
	}
	
	
	@Override
	protected boolean subInit() {
		if (!hasInit) { // Each music command (play, skip, etc.) will call this. Only want to run it once.
			hasInit = true;
			AudioSourceManagers.registerRemoteSources(playerManager);
			AudioSourceManagers.registerLocalSource(playerManager);
			player.setVolume(Globals.MUSIC_VOLUME);
			player.addListener(trackScheduler);
			
			if (!musicFolder.exists() || !musicFolder.isDirectory()) {
				logger.error("Could not find music directory");
				return false;
			}
			
			File[] files = musicFolder.listFiles();
			if (files == null) return true;
//			Arrays.parallelSort(files);
			
			for (File file : files) {
				if (file.isDirectory()) {
					albums.put(file.getName(), new LinkedList<>());
					
					File[] albumSongs = file.listFiles();
					if (albumSongs == null) continue;
//					Arrays.parallelSort(albumSongs);
					
					for (File song : albumSongs) {
						String name = song.getName().split("\\.")[0]; // Removes extension
						albums.get(file.getName()).add(song.getAbsolutePath()); // Uses intuitive song title
						songs.put(name, song.getAbsolutePath());
						logger.info("Found song: " + song);
					}
				} else {
					songs.put(file.getName().split("\\.")[0], file.getAbsolutePath());
				}
			}
			
		}
		return true;
	}
	
	
	@Override
	protected final boolean subEnd() {
		playerManager.shutdown();
		return true;
	}
	
}
