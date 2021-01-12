package com.rotemati.foregroundsdk.foregroundtask.internal.repositories

import androidx.annotation.WorkerThread
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.internal.db.TaskToDBItemConvertor
import com.rotemati.foregroundsdk.foregroundtask.internal.db.TasksDBHolder

internal class PendingTasksRepository {

	private val dao = TasksDBHolder.foregroundTaskInfoDao
	private val taskToDBItemConvertor = TaskToDBItemConvertor()

	@WorkerThread
	fun getAll(): List<TaskInfoSpec> {
		return dao.getAll().map {
			TaskInfoSpec(taskToDBItemConvertor.fromDBItem(it), it.componentName)
		}
	}

	@WorkerThread
	fun getById(id: Int): TaskInfoSpec? {
		val foregroundTaskInfoDBItem = dao.getById(id)
		return foregroundTaskInfoDBItem?.let {
			TaskInfoSpec(taskToDBItemConvertor.fromDBItem(it), it.componentName)
		}
	}

	@WorkerThread
	fun remove(foregroundTaskInfo: ForegroundTaskInfo) {
		val savedTaskInfo = dao.getById(foregroundTaskInfo.id)
		savedTaskInfo?.let {
			dao.delete(savedTaskInfo)
		}
	}

	@WorkerThread
	fun save(taskInfoSpec: TaskInfoSpec) {
		val dbItem = taskToDBItemConvertor.toDBItem(
				taskInfoSpec.foregroundTaskInfo,
				taskInfoSpec.componentName
		)
		dao.insert(dbItem)
	}
}

internal data class TaskInfoSpec(
		val foregroundTaskInfo: ForegroundTaskInfo,
		val componentName: String
)