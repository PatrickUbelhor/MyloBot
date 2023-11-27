package clients;

import main.Config;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.annotation.Nonnull;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Patrick Ubelhor
 * @version 3/24/2023
 */
public class VoiceTrackerClient {
	
	private static final Logger logger = LogManager.getLogger(VoiceTrackerClient.class);
	
	private final OkHttpClient client;
	private final MediaType JSON = MediaType.get("application/json; charset=utf-8");
	private final String bodyFormat = "{\"leavingChannelId\": \"%s\",\"joiningChannelId\": \"%s\"}";
	
	public VoiceTrackerClient() {
		client = new OkHttpClient.Builder()
			.connectTimeout(60, TimeUnit.SECONDS)
			.readTimeout(60, TimeUnit.SECONDS)
			.writeTimeout(60, TimeUnit.SECONDS)
			.build();
	}
	
	
	public void logJoinEvent(Long userSnowflake, Long channelId) {
		String body = String.format(bodyFormat, "null", channelId);
		String baseUrl = Config.getConfig().url().VOICE_TRACKER();
		Request request = new Request.Builder()
				.url(baseUrl + "/join/" + userSnowflake)
				.post(RequestBody.create(body, JSON))
				.build();
		
		// Send JOIN request to VoiceTracker asynchronously
		client.newCall(request).enqueue(new VoiceTrackerCallback("JOIN"));
	}
	
	
	public void logMoveEvent(Long snowflake, Long leavingChannelId, Long joiningChannelId) {
		String body = String.format(bodyFormat, leavingChannelId, joiningChannelId);
		String baseUrl = Config.getConfig().url().VOICE_TRACKER();
		Request request = new Request.Builder()
				.url(baseUrl + "/move/" + snowflake)
				.post(RequestBody.create(body, JSON))
				.build();
		
		client.newCall(request).enqueue(new VoiceTrackerCallback("MOVE"));
	}
	
	
	public void logLeaveEvent(Long userSnowflake, Long channelId) {
		String body = String.format(bodyFormat, channelId, "null");
		String baseUrl = Config.getConfig().url().VOICE_TRACKER();
		Request request = new Request.Builder()
				.url(baseUrl + "/leave/" + userSnowflake)
				.post(RequestBody.create(body, JSON))
				.build();
		
		client.newCall(request).enqueue(new VoiceTrackerCallback("LEAVE"));
	}
	
	
	private static class VoiceTrackerCallback implements Callback {
		final String type;
		
		private VoiceTrackerCallback(String type) {
			this.type = type;
		}
		
		@Override
		public void onFailure(@Nonnull Call call, @Nonnull IOException e) {
			logger.error("[Voice] Failed to send {} request", type, e);
		}
		
		@Override
		public void onResponse(@Nonnull Call call, @Nonnull Response response) {
			if (!response.isSuccessful()) {
				logger.error("[Voice] Error on {} request: {}\n{}", type, response.code(), response.body());
				return;
			}
			
			response.close();
			logger.debug("[Voice] Successfully sent {} request", type);
		}
	}
	
}
