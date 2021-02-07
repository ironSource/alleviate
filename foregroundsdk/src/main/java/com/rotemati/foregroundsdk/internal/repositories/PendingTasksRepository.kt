package com.rotemati.foregroundsdk.internal.repositories

import android.content.Context
import com.rotemati.foregroundsdk.external.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.internal.prefs.SharedPreferencesProxy

private const val PREFS_FILE = "com.rotemati.TASKS_REPO_FILE"

internal class PendingTasksRepository(context: Context) {

	private val dbItemToTaskInfoConverter = TaskDBItemToTaskInfoConverter()
	private val dataStore = SharedPreferencesProxy(context, PREFS_FILE)

	fun getAll(): List<TaskInfoSpec> {
		return dataStore.getAll().map {
			dbItemToTaskInfoConverter.toTaskInfo(it)
		}
	}

	fun getTaskInfo(id: Int): TaskInfoSpec? {
		val foregroundTaskInfoDBItem = dataStore.getById(id)
		return foregroundTaskInfoDBItem?.let {
			dbItemToTaskInfoConverter.toTaskInfo(it)
		}
	}

	fun delete(id: Int) {
		dataStore.delete(id)
	}

	fun insert(taskInfoSpec: TaskInfoSpec) {
		val dbItem = dbItemToTaskInfoConverter.toDBItem(taskInfoSpec)
		dataStore.insert(dbItem)
	}
}

internal data class TaskInfoSpec(
		val foregroundTaskInfo: ForegroundTaskInfo,
		val componentName: String
)