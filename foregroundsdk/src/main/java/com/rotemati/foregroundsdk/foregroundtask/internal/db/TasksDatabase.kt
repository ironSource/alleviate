package com.rotemati.foregroundsdk.foregroundtask.internal.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ForegroundTaskInfoDBItem::class], version = 1)
@TypeConverters(Converters::class)
abstract class TasksDatabase : RoomDatabase() {
	abstract fun foregroundTaskInfoDao(): ForegroundTaskInfoDao
}