package com.rotemati.foregroundsdk.internal.prefs

import android.content.Context
import com.rotemati.foregroundsdk.internal.datastore.ForegroundTaskInfoDBItem
import com.rotemati.foregroundsdk.internal.datastore.JsonToDBItemConverter
import org.json.JSONObject

internal class SharedPreferencesProxy(context: Context, prefName: String) {

	private val sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
	private val tasksToDBItemConverter = JsonToDBItemConverter()

	fun getById(id: Int): ForegroundTaskInfoDBItem? {
		return sharedPreferences.getString(id.toString(), null)?.let { stringRepresentation ->
			tasksToDBItemConverter.fromJson(JSONObject(stringRepresentation))
		}
	}

	fun getAll(): List<ForegroundTaskInfoDBItem> {
		return ArrayList<ForegroundTaskInfoDBItem>().apply {
			sharedPreferences.all.forEach { pref ->
				val foregroundTaskInfoDBItem = tasksToDBItemConverter.fromJson(JSONObject(pref.value.toString()))
				foregroundTaskInfoDBItem?.let {
					add(it)
				}
			}
		}
	}

	fun insert(taskInfo: ForegroundTaskInfoDBItem) {
		sharedPreferences.edit().putString(taskInfo.id.toString(), tasksToDBItemConverter.toJson(taskInfo).toString()).apply()
	}

	fun delete(id: Int) {
		sharedPreferences.edit().remove(id.toString()).apply()
	}
}