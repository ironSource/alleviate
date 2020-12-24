package com.rotemati.foregroundsdk.sharedprefs

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.rotemati.foregroundsdk.ForegroundSDK
import com.rotemati.foregroundsdk.logger.ForegroundLogger
import com.rotemati.foregroundsdk.logger.LoggerWrapper
import java.lang.reflect.Type

internal class SharedPreferencesProxy(context: Context, prefName: String) {

	private val logger: ForegroundLogger = LoggerWrapper(ForegroundSDK.foregroundLogger)

	private val gson = Gson()
	private val sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)

	fun setObject(key: String?, obj: Any?, shouldCommitImmediately: Boolean = false) {
		requireNotNull(obj) { "Cannot put 'null' object into preferences" }
		val str = gson.toJson(obj)
		val editor: SharedPreferences.Editor = sharedPreferences.edit()
		editor.putString(key, str)
		save(editor, shouldCommitImmediately)
	}

	fun <T> getObject(key: String, classType: Type): T? {
		return try {
			gson.fromJson(sharedPreferences.getString(key, null), classType)
		} catch (e: JsonSyntaxException) {
			logger.e("Failed to retrieve " + classType + " from cache: " + e.message)
			null
		}
	}

	fun getString(key: String) = sharedPreferences.getString(key, null)

	fun setString(key: String, value: String, shouldCommitImmediately: Boolean = false) {
		val editor: SharedPreferences.Editor = sharedPreferences.edit()
		editor.putString(key, value)
		save(editor, shouldCommitImmediately)
	}

	fun remove(key: String) = sharedPreferences.edit().remove(key).apply()

	fun all(): Map<String, *> = sharedPreferences.all

	fun clear() = sharedPreferences.edit().clear().apply()

	private fun save(editor: SharedPreferences.Editor, shouldCommitImmediately: Boolean) {
		if (shouldCommitImmediately) {
			editor.commit()
		} else {
			editor.apply()
		}
	}
}