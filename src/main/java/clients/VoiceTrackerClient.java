package clients;

import main.Globals;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author Patrick Ubelhor
 * @version 2/12/2021
 */
public class VoiceTrackerClient {
	
	private final OkHttpClient client;
	private final MediaType JSON = MediaType.get("application/json; charset=utf-8");
	private final String bodyFormat = "{\"leavingChannelId\": \"%s\",\"joiningChannelId\": \"%s\"}";
	
	public VoiceTrackerClient() {
		client = new OkHttpClient();
	}
	
	
	public void logJoinEvent(Long userSnowflake, Long channelId) {
		String body = String.format(bodyFormat, "null", channelId);
		Request request = new Request.Builder()
				.url(Globals.VOICE_TRACKER_BASE_URL + "/join/" + userSnowflake)
				.post(RequestBody.create(body, JSON))
				.build();
		
		// Send JOIN request to VoiceTracker asynchronously
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				Globals.logger.error("Failed to send JOIN request", e);
			}
			
			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) {
				if (!response.isSuccessful()) {
					Globals.logger.error("Error on JOIN request: {}\n{}", response.code(), response.body());
					return;
				}
				
				response.close();
				Globals.logger.debug("Successfully sent JOIN request");
			}
		});
	}
	
	
	public void logMoveEvent(Long snowflake, Long leavingChannelId, Long joiningChannelId) {
		String body = String.format(bodyFormat, leavingChannelId, joiningChannelId);
		Request request = new Request.Builder()
				.url(Globals.VOICE_TRACKER_BASE_URL + "/move/" + snowflake)
				.post(RequestBody.create(body, JSON))
				.build();
		
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				Globals.logger.error("Failed to send MOVE request", e);
			}
			
			@Override
			public void onResponse(Call call, Response response) {
				if (!response.isSuccessful()) {
					Globals.logger.error("Error on MOVE request: {}\n{}", response.code(), response.body());
					return;
				}
				
				response.close();
				Globals.logger.debug("Successfully sent MOVE request");
			}
		});
	}
	
	
	public void logLeaveEvent(Long userSnowflake, Long channelId) {
		String body = String.format(bodyFormat, channelId, "null");
		Request request = new Request.Builder()
				.url(Globals.VOICE_TRACKER_BASE_URL + "/leave/" + userSnowflake)
				.post(RequestBody.create(body, JSON))
				.build();
		
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				Globals.logger.error("Failed to send LEAVE request", e);
			}
			
			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) {
				if (!response.isSuccessful()) {
					Globals.logger.error("Error on LEAVE request: {}\n{}", response.code(), response.body());
					return;
				}
				
				response.close();
				Globals.logger.debug("Successfully sent LEAVE request");
			}
		});
	}
	
}
