package com.rotemati.foregroundsdk.internal.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ForegroundTaskInfoDBItem::class], version = 1)
@TypeConverters(Converters::class)
internal abstract class TasksDatabase : RoomDatabase() {
	abstract fun foregroundTaskInfoDao(): ForegroundTaskInfoDao
}