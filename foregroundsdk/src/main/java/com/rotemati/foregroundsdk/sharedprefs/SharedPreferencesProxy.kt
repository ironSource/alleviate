package com.rotemati.foregroundsdk.sharedprefs

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.rotemati.foregroundsdk.logger.SDKLogger
import java.lang.reflect.Type

class SharedPreferencesProxy(context: Context, prefName: String) {

    private val gson = Gson()
    private val sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)

    fun setSharedPrefObject(key: String?, obj: Any?, shouldCommitImmediately: Boolean = false) {
        requireNotNull(obj) { "Cannot put 'null' object into preferences" }
        val str = convertObjectToJsonString(obj)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(key, str)
        doSaveEdits(editor, shouldCommitImmediately)
    }

    fun <T> getSharedPrefObject(key: String, classType: Type): T? {
        return convertJsonStringToObject(sharedPreferences.getString(key, null), classType)
    }

    fun remove(key: String) = sharedPreferences.edit().remove(key).apply()

    fun all(): Map<String, *> = sharedPreferences.all

    fun clear() = sharedPreferences.edit().clear().apply()

    private fun doSaveEdits(editor: SharedPreferences.Editor, shouldCommitImmediately: Boolean) {
        if (shouldCommitImmediately) {
            editor.commit()
        } else {
            editor.apply()
        }
    }

    private fun convertObjectToJsonString(obj: Any): String {
        return gson.toJson(obj)
    }

    private fun <T> convertJsonStringToObject(jsonString: String?, classType: Type): T? {
        return try {
            gson.fromJson(jsonString, classType)
        } catch (e: JsonSyntaxException) {
            SDKLogger.e("Failed to retrieve " + classType + " from cache: " + e.message)
            null
        }
    }
}