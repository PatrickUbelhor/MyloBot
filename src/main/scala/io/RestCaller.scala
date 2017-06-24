package io

import java.io.IOException
import java.net.{HttpURLConnection, MalformedURLException, URL}
import java.util.LinkedHashMap

/**
  * An object for making Restful API calls.
  */
@throws[MalformedURLException]
private class RestCaller() {
	
	private val headerProperties = new LinkedHashMap[String, String]()
	
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
	  * @param url The URL to hit with the HTTP request.
	  */
	@throws[IOException]
	def request(method: RequestType, url: String): HttpResponse = {
		
		val connection: HttpURLConnection = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
		var response: HttpResponse = null
		
		try {
			
			connection.setRequestMethod(method.get)
			
			// Apply all the request properties
			// TODO: Find a less disgusting way to iterate over a set. Maybe don't use Java.util data structures
			for (key <- headerProperties.keySet().toArray(new Array[String](0))) {
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