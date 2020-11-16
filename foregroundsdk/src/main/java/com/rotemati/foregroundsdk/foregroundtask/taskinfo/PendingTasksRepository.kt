package com.rotemati.foregroundsdk.foregroundtask.taskinfo

import android.content.Context
import com.rotemati.foregroundsdk.sharedprefs.SharedPreferencesProxy

private const val PREFS_FILE = "com.rotemati.TASK_REPO_FILE"

internal class PendingTasksRepository(context: Context) {

	private val sharedPrefs = SharedPreferencesProxy(context, PREFS_FILE)

	val pendingForegroundTasks: List<ForegroundTaskInfo>
		get() = sharedPrefs.all().keys.mapNotNull {
			sharedPrefs.getSharedPrefObject<ForegroundTaskInfo>(it, ForegroundTaskInfo::class.java)
		}

	fun remove(id: Int) {
		sharedPrefs.remove(id.toString())
	}

	fun save(pendingForegroundTask: ForegroundTaskInfo) {
		sharedPrefs.setSharedPrefObject(pendingForegroundTask.id.toString(), pendingForegroundTask)
	}

	fun contains(id: Int): Boolean {
		return pendingForegroundTasks.any { it.id == id }
	}
}