package com.rotemati.foregroundsdk.foregroundtask.internal.db

import androidx.room.Room
import com.rotemati.foregroundsdk.foregroundtask.external.ForegroundSDK

internal object TasksDBHolder {

	val db: TasksDatabase
		get() = Room.databaseBuilder(
				ForegroundSDK.context,
				TasksDatabase::class.java, "tasks_db")
				.build()
}