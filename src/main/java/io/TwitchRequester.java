package io;

import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static main.Globals.logger;

/**
 * @author Patrick Ubelhor
 * @version 06/14/2018
 */
public class TwitchRequester {
	
	private static final String USER_LOOKUP = "https://api.twitch.tv/kraken/users?login=%s";
	private static final String ID_LOOKUP = "https://api.twitch.tv/kraken/streams/%s";
	private static final String TWITCH_JSON = "application/vnd.twitchtv.v5+json";

	
	private final String clientId;
	
	public TwitchRequester(String clientId) {
		this.clientId = clientId;
	}
	
	
	/**
	 * Given the username of the streamer, returns the unique user ID of the streamer.
	 *
	 * @param username The display name of the streamer.
	 * @return The unique user ID of the streamer.
	 */
	public String getUserId(String username) throws IOException, InterruptedException, URISyntaxException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(new URI(String.format(USER_LOOKUP, username)))
				.GET()
				.header("Accept", TWITCH_JSON)
				.header("Client-ID", clientId)
				.build();
		
		HttpResponse<String> response = HttpClient.newBuilder()
				.build()
				.send(request, HttpResponse.BodyHandler.asString());
		
		return new JSONObject(response.body())
				.getJSONArray("users")
				.getJSONObject(0)
				.getString("_id");
	}
	
	
	/**
	 * Given the ID of the streamer, returns an Option containing the link to the stream, or empty if
	 * the stream is offline.
	 *
	 * @param userId The unique user ID of the streamer.
	 * @return A link to the stream, or empty if it's offline.
	 */
	public String getStream(String userId) {
		
		HttpRequest request;
		try {
			request = HttpRequest.newBuilder()
					.uri(new URI(String.format(ID_LOOKUP, userId)))
					.GET()
					.header("Accept", TWITCH_JSON)
					.header("Client-ID", clientId)
					.build();
		} catch (URISyntaxException e) {
			logger.error("Bad URL", e);
			return null;
		}
		
		HttpResponse<String> response;
		try {
			response = HttpClient.newBuilder()
					.build()
					.send(request, HttpResponse.BodyHandler.asString());
		} catch (IOException | InterruptedException e) {
			logger.error(e);
			return null;
		}
		
		var content = new JSONObject(response.body()).getJSONObject("stream");
		
		return content.getJSONObject("channel").getString("url");
	}

}
