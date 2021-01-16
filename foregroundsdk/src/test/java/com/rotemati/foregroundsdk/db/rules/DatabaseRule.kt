package com.rotemati.foregroundsdk.db.rules

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rotemati.foregroundsdk.foregroundtask.internal.db.ForegroundTaskInfoDBItem
import com.rotemati.foregroundsdk.foregroundtask.internal.db.ForegroundTaskInfoDao
import com.rotemati.foregroundsdk.foregroundtask.internal.db.TasksDatabase
import org.junit.rules.ExternalResource

internal class DatabaseRule : ExternalResource() {

	private lateinit var tasksDao: ForegroundTaskInfoDao
	private lateinit var db: TasksDatabase

	fun insert(taskInfo: ForegroundTaskInfoDBItem) {
		tasksDao.insert(taskInfo)
	}

	fun getById(id: Int) = tasksDao.getById(id)

	fun delete(taskInfo: ForegroundTaskInfoDBItem) = tasksDao.delete(taskInfo)

	fun getAll() = tasksDao.getAll()

	override fun before() {
		// create db
		val context = ApplicationProvider.getApplicationContext<Context>()
		db = Room.inMemoryDatabaseBuilder(
                context, TasksDatabase::class.java
        ).allowMainThreadQueries().build()
		tasksDao = db.foregroundTaskInfoDao()
	}

	override fun after() {
//        tasksDao.deleteAll()
		db.close()
	}
}