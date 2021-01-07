package com.rotemati.foregroundsdk.foregroundtask.internal.repositories

//import com.rotemati.foregroundsdk.foregroundtask.internal.db.TasksDBHolder
import android.content.Context
import androidx.annotation.WorkerThread
import androidx.room.Room
import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.ForegroundTaskInfo
import com.rotemati.foregroundsdk.foregroundtask.internal.db.TaskToDBItemConvertor
import com.rotemati.foregroundsdk.foregroundtask.internal.db.TasksDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@WorkerThread
internal class PendingTasksRepository(context: Context) {

	val db = Room.databaseBuilder(
            context,
            TasksDatabase::class.java, "database-name"
    ).build()
	private val dao = db.foregroundTaskInfoDao()
	private val taskToDBItemConvertor = TaskToDBItemConvertor()

	private val executor: ExecutorService = Executors.newSingleThreadExecutor()

	fun getAll(callback: (List<ForegroundTaskInfo>) -> Unit) {
		executor.execute {
			val tasks = dao.getAll().map {
				taskToDBItemConvertor.fromDBItem(it)
			}
			callback(tasks)
		}
	}

	fun getById(id: Int, callback: (ForegroundTaskInfo?) -> Unit) {
		executor.execute {
			val foregroundTaskInfoDBItem = dao.getById(id)
			val taskDBItem = foregroundTaskInfoDBItem?.let {
				taskToDBItemConvertor.fromDBItem(it)
			}
			callback(taskDBItem)
		}
	}

	fun remove(foregroundTaskInfo: ForegroundTaskInfo) {
		executor.execute {
			val savedTaskInfo = dao.getById(foregroundTaskInfo.id)
			savedTaskInfo?.let {
				dao.delete(savedTaskInfo)
			}
		}
	}

	fun save(taskInfoSpec: TaskInfoSpec) {
		executor.execute {
			val dbItem = taskToDBItemConvertor.toDBItem(
                    taskInfoSpec.foregroundTaskInfo,
                    taskInfoSpec.componentName
            )
			dao.insert(dbItem)
		}
	}

	fun getTaskInfoSpec(id: Int): TaskInfoSpec? {
		return dao.getById(id)?.let {
			TaskInfoSpec(
                    taskToDBItemConvertor.fromDBItem(it),
                    it.componentName
            )
		}
	}
}

data class TaskInfoSpec(
        val foregroundTaskInfo: ForegroundTaskInfo,
        val componentName: String
)

sealed class Result<out R> {
	data class Success<out T>(val data: T) : Result<T>()
	data class Error(val exception: Exception) : Result<Nothing>()
}