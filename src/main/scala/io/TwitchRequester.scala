package io

import org.json.{JSONException, JSONObject}

/**
  * @author PatrickUbelhor
  * @version 06/24/2017
  */
class TwitchRequester(clientId: String) {
	
	private val caller = new RestCaller()
	
	caller.addProperty("Accept", MimeType.TWITCH_JSON.get)
	caller.addProperty("Client-ID", clientId)
	
	def getUserId(username: String): String = {
		val response = caller.request(
			RequestType.GET,
			String.format("https://api.twitch.tv/kraken/users?login=%s", username)
		)
		
		new JSONObject(response.getContent).getJSONArray("users").getJSONObject(0).getString("_id")
	}
	
	
	def getStream(userId: String): Option[String] = {
		var result: Option[String] = Option.empty
		
		val response = caller.request(
			RequestType.GET,
			String.format("https://api.twitch.tv/kraken/streams/%s", userId)
		)
		
		try {
			val content = new JSONObject(response.getContent).getJSONObject("stream")
			result = Option(content.getJSONObject("channel").getString("url"))
		} catch {
			case ex: JSONException => ()
		}
		
		result
	}
	
}
