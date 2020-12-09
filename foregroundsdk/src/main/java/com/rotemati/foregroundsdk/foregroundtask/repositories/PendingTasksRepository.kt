package com.rotemati.foregroundsdk.foregroundtask.repositories

import android.content.Context
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.sharedprefs.SharedPreferencesProxy

private const val PREFS_FILE = "com.rotemati.TASK_REPO_FILE"

internal class PendingTasksRepository(context: Context) {

	private val sharedPrefs = SharedPreferencesProxy(context, PREFS_FILE)

	val pendingForegroundTasks: List<ForegroundTaskInfo>
		get() = sharedPrefs.all().keys.mapNotNull {
			sharedPrefs.getObject<ForegroundTaskInfo>(it, ForegroundTaskInfo::class.java)
		}

	fun remove(id: Int) {
		sharedPrefs.remove(id.toString())
	}

	fun save(pendingForegroundTask: ForegroundTaskInfo, componentName: String) {
		val key = pendingForegroundTask.id.toString()
		sharedPrefs.setObject(key, pendingForegroundTask)
		sharedPrefs.setString(key, componentName)
	}

	fun getComponent(id: Int): String? = sharedPrefs.getString(id.toString())

	fun contains(id: Int): Boolean {
		return pendingForegroundTasks.any { it.id == id }
	}
}