package io

import java.io.IOException
import java.net.{HttpURLConnection, MalformedURLException, URL}
import java.util.LinkedHashMap

/**
  * An object for making Restful API calls.
  *
  * @param address The URL to hit with the HTTP request.
  * @param sendType The file format with which to send data.
  * @param receiveType The file format with which data should be received.
  */
@throws[MalformedURLException]
private class RestCaller(address: String, sendType: MimeType, receiveType: MimeType) {
	
	private val url = new URL(url)
	private val headerProperties = new LinkedHashMap[String, String]()
	
	headerProperties.put("Accept", sendType.get())
	headerProperties.put("Content-type", receiveType.get())
	
	
	/**
	  * Add an additional property to the header of the HTTP request.
	  *
	  * @param key The property key.
	  * @param value The property value.
	  */
	def addProperty(key: String, value: String): Unit = {
		headerProperties.put(key, value)
	}
	
	
	/**
	  * Make a Restful API call with an empty content body.
	  *
	  * @param method The HTTP request method type.
	  */
	@throws[IOException]
	def request(method: RequestType): HttpResponse = {
		
		val connection: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
		var response: HttpResponse = null
		
		try {
			
			connection.setRequestMethod(method.get())
			
			// Apply all the request properties
			for (key: String <- headerProperties.keySet()) {
				connection.setRequestProperty(key, headerProperties.get(key))
			}
			
			connection.connect()
			
			val responseBuilder = new HttpResponseBuilder()
					.setCode(connection.getResponseCode)
					.setMessage(connection.getResponseMessage)
		        	.setContent(scala.io.Source.fromInputStream(connection.getInputStream).mkString)
			
			var i: Int = 0
			while (connection.getHeaderFieldKey(i) != null) {
				responseBuilder.setHeaderProperty(connection.getHeaderFieldKey(i), connection.getHeaderField(i))
				i += 1
			}
			
			response = responseBuilder.build
			
		} catch {
			case ex: IOException => ex.printStackTrace()
		} finally {
			connection.disconnect()
		}
		
		response
	}
	
}