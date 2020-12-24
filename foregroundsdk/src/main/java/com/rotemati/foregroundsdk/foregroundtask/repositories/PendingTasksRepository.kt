package com.rotemati.foregroundsdk.foregroundtask.repositories

import android.content.Context
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.sharedprefs.SharedPreferencesProxy

private const val TASK_PREFS_FILE = "com.rotemati.TASK_REPO_FILE"
private const val COMPONENT_PREFS_FILE = "com.rotemati.COMPONENT_REPO_FILE"

internal class PendingTasksRepository(context: Context) {

	private val tasksSharedPrefs = SharedPreferencesProxy(context, TASK_PREFS_FILE)
	private val componentSharedPrefs = SharedPreferencesProxy(context, COMPONENT_PREFS_FILE)

	val foregroundTasks: List<ForegroundTaskInfo>
		get() = tasksSharedPrefs.all().keys.mapNotNull {
			tasksSharedPrefs.getObject<ForegroundTaskInfo>(it, ForegroundTaskInfo::class.java)
		}

	fun remove(id: Int) {
		tasksSharedPrefs.remove(id.toString())
		componentSharedPrefs.remove(id.toString())
	}

	fun save(pendingForegroundTask: ForegroundTaskInfo, componentName: String) {
		val key = pendingForegroundTask.id.toString()
		tasksSharedPrefs.setObject(key, pendingForegroundTask)
		componentSharedPrefs.setString(key, componentName)
	}

	fun getComponent(id: Int): String? = componentSharedPrefs.getString(id.toString())

	fun contains(id: Int): Boolean {
		return foregroundTasks.any { it.id == id } && !getComponent(id).isNullOrEmpty()
	}
}