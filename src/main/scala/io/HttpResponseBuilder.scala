package io

import java.util.LinkedHashMap

/**
  * @author PatrickUbelhor
  * @version 06/24/2017
  */
private class HttpResponseBuilder {
	
	private var code: Option[Int] = Option.empty
	private var message: Option[String] = Option.empty
	private var content: Option[String] = Option.empty
	private val headerProperties = new LinkedHashMap[String, String]
	
	/**
	  * @param code The HTTP response code.
	  * @return This builder, for chaining method calls.
	  */
	def setCode(code: Int): HttpResponseBuilder = {
		this.code = Option(code)
		this
	}
	
	/**
	  * @param message The HTTP response message.
	  * @return This builder, for chaining method calls.
	  */
	def setMessage(message: String): HttpResponseBuilder = {
		this.message = Option(message)
		this
	}
	
	/**
	  * @param content The content of the HTTP response.
	  * @return This builder, for chaining method calls.
	  */
	def setContent(content: String): HttpResponseBuilder = {
		this.content = Option(content)
		this
	}
	
	/**
	  * @param key The key of the header property in the HTTP response.
	  * @param value The value of the header property in the HTTP response.
	  * @return This builder, for chaining method calls.
	  */
	def setHeaderProperty(key: String, value: String): HttpResponseBuilder = {
		headerProperties.put(key, value)
		this
	}
	
	/**
	  * Builds the HttpResponse object.
	  *
	  * @return An HttpResponse containing the previously defined fields.
	  */
	def build: HttpResponse = {
		if (code.isEmpty) throw new RuntimeException("Response code not set")
		if (message.isEmpty) throw new RuntimeException("Response message not set")
		
		new HttpResponse(code.get, message.get, content.get, headerProperties)
	}
	
}
