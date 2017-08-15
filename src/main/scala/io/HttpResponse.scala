package io

import java.util.LinkedHashMap

/**
  * @author PatrickUbelhor
  * @version 06/23/2017
  */
private class HttpResponse(code: Int, message: String, content: String, headerProperties: LinkedHashMap[String, String]) {
	
	def getCode: Int = code
	def getMessage: String = message
	def getContent: String = content
	def getHeaderProperty(key: String): String = headerProperties.get(key)
	
}
