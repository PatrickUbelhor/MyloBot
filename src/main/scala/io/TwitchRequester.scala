package io

import java.io.IOException

import org.json.{JSONException, JSONObject}

/**
  * @author PatrickUbelhor
  * @version 7/1/2017
  */
class TwitchRequester(clientId: String) {
	
	private val caller = new RestCaller()
	
	caller.addProperty("Accept", MimeType.TWITCH_JSON.get)
	caller.addProperty("Client-ID", clientId)
	
	/**
	  * Given the username of the streamer, returns the unique user ID of the streamer.
	  *
	  * @param username The display name of the streamer.
	  * @return The unique user ID of the streamer.
	  */
	def getUserId(username: String): String = {
		val response = caller.request(
			RequestType.GET,
			String.format("https://api.twitch.tv/kraken/users?login=%s", username)
		)
		
		new JSONObject(response.getContent).getJSONArray("users").getJSONObject(0).getString("_id")
	}
	
	
	/**
	  * Given the ID of the streamer, returns an Option containing the link to the stream, or empty if
	  * the stream is offline.
	  *
	  * @param userId The unique user ID of the streamer.
	  * @return A link to the stream, or empty if it's offline.
	  */
	def getStream(userId: String): Option[String] = {
		var result: Option[String] = Option.empty
		
		val response = caller.request(
			RequestType.GET,
			String.format("https://api.twitch.tv/kraken/streams/%s", userId)
		)
		
		// stream can equal null, hence the try block
		try {
			val content = new JSONObject(response.getContent).getJSONObject("stream")
			result = Option(content.getJSONObject("channel").getString("url"))
		} catch {
			case ex: JSONException => ()
		}
		
		result
	}
	
}
