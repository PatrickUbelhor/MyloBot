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
 * @version 2/18/2020
 */
public class VoiceTrackerClient {
	
	private final OkHttpClient client;
	private final MediaType JSON = MediaType.get("application/json; charset=utf-8");
	
	public VoiceTrackerClient() {
		client = new OkHttpClient();
	}
	
	
	public void logJoinEvent(Long snowflake) {
		
		Request request = new Request.Builder()
				.url(Globals.VOICE_TRACKER_BASE_URL + "/join/" + snowflake)
				.post(RequestBody.create("", JSON))
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
					Globals.logger.error("Error on JOIN request: " + response.code() + "\n" + response.body());
					return;
				}
				
				response.close();
				Globals.logger.debug("Successfully sent JOIN request");
			}
		});
	}
	
	
	public void logLeaveEvent(Long snowflake) {
		
		Request request = new Request.Builder()
				.url(Globals.VOICE_TRACKER_BASE_URL + "/leave/" + snowflake)
				.post(RequestBody.create("", JSON))
				.build();
		
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				Globals.logger.error("Failed to send LEAVE request", e);
			}
			
			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) {
				if (!response.isSuccessful()) {
					Globals.logger.error("Error on LEAVE request: " + response.code() + "\n" + response.body());
					return;
				}
				
				response.close();
				Globals.logger.debug("Successfully sent LEAVE request");
			}
		});
	}
	
}
