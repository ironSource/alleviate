package com.rotemati.foregroundsdk.foregroundtask.internal.repositories

import androidx.annotation.WorkerThread
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.internal.db.ForegroundTaskInfoDBItem
import com.rotemati.foregroundsdk.foregroundtask.internal.db.TaskToDBItemConvertor
import com.rotemati.foregroundsdk.foregroundtask.internal.db.TasksDBHolder

internal class PendingTasksRepository {

	private val db = TasksDBHolder.db
	private val taskToDBItemConvertor = TaskToDBItemConvertor()

	@WorkerThread
	fun getAll(): List<TaskInfoSpec> {
		return db.foregroundTaskInfoDao().getAll().map {
			TaskInfoSpec(taskToDBItemConvertor.fromDBItem(it), it.componentName)
		}.also { db.close() }
	}

	@WorkerThread
	fun getTaskInfo(id: Int): TaskInfoSpec? {
		val foregroundTaskInfoDBItem = db.foregroundTaskInfoDao().getById(id)
		return foregroundTaskInfoDBItem?.let {
			TaskInfoSpec(taskToDBItemConvertor.fromDBItem(it), it.componentName)
		}.also { db.close() }
	}

	@WorkerThread
	fun getDBItem(id: Int): ForegroundTaskInfoDBItem? {
		return db.foregroundTaskInfoDao().getById(id).also { db.close() }
	}

	@WorkerThread
	fun remove(foregroundTaskInfo: ForegroundTaskInfo) {
		val savedTaskInfo = db.foregroundTaskInfoDao().getById(foregroundTaskInfo.id)
		savedTaskInfo?.let {
			db.foregroundTaskInfoDao().delete(savedTaskInfo)
		}.also { db.close() }
	}

	@WorkerThread
	fun save(taskInfoSpec: TaskInfoSpec) {
		val timestamp = System.currentTimeMillis()
		val dbItem = taskToDBItemConvertor.toDBItem(
				timestamp,
				taskInfoSpec.foregroundTaskInfo,
				taskInfoSpec.componentName
		)
		db.foregroundTaskInfoDao().insert(dbItem).also { db.close() }
	}
}

internal data class TaskInfoSpec(
		val foregroundTaskInfo: ForegroundTaskInfo,
		val componentName: String
)