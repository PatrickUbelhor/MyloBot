package clients;

import main.Globals;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author Patrick Ubelhor
 * @version 12/17/2019
 */
public class VoiceTrackerClient {
	
	private final HttpClient client;
	
	public VoiceTrackerClient() {
		this.client = HttpClient.newHttpClient();
	}
	
	
	public void logJoinEvent(Long snowflake) {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(Globals.VOICE_TRACKER_BASE_URL + "/join/" + snowflake))
				.POST(HttpRequest.BodyPublishers.noBody())
				.build();
		
		client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
	}
	
	public void logLeaveEvent(Long snowflake) {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(Globals.VOICE_TRACKER_BASE_URL + "/leave/" + snowflake))
				.POST(HttpRequest.BodyPublishers.noBody())
				.build();
		
		client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
	}
	
}
