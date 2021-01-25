package com.rotemati.foregroundsdk.db.rules

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rotemati.foregroundsdk.internal.db.ForegroundTaskInfoDao
import com.rotemati.foregroundsdk.internal.db.TasksDatabase
import org.junit.rules.ExternalResource

internal class DatabaseRule : ExternalResource() {

	lateinit var tasksDao: ForegroundTaskInfoDao
	private lateinit var db: TasksDatabase

	override fun before() {
		// create db
		val context = ApplicationProvider.getApplicationContext<Context>()
		db = Room.inMemoryDatabaseBuilder(
				context, TasksDatabase::class.java
		).allowMainThreadQueries().build()
		tasksDao = db.foregroundTaskInfoDao()
	}

	override fun after() {
		db.close()
	}
}