package com.rotemati.foregroundsdk.adapters

import com.google.gson.*
import java.lang.reflect.Type

private const val CLASSNAME = "CLASSNAME"
private const val DATA = "DATA"

class InterfaceAdapter : JsonSerializer<Any>, JsonDeserializer<Any> {

	override fun serialize(
            src: Any,
            typeOfSrc: Type,
            context: JsonSerializationContext
    ): JsonElement {
		val jsonObject = JsonObject()
		jsonObject.addProperty(CLASSNAME, src.javaClass.name)
		jsonObject.add(DATA, context.serialize(src))
		return jsonObject
	}

	override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
    ): Any {
		val jsonObject: JsonObject = json.asJsonObject
		val prim = jsonObject[CLASSNAME] as JsonPrimitive
		val className = prim.asString
		val klass: Class<*> = getObjectClass(className)
		return context.deserialize(jsonObject[DATA], klass)
	}

	private fun getObjectClass(className: String): Class<*> {
		return try {
			Class.forName(className)
		} catch (e: ClassNotFoundException) {
			throw JsonParseException(e.message)
		}
	}
}