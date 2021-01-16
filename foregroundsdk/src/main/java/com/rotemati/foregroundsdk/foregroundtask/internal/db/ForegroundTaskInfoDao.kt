package com.rotemati.foregroundsdk.foregroundtask.internal.db

import androidx.room.*

@Dao
internal interface ForegroundTaskInfoDao {

	@Query("SELECT * FROM tasks")
	fun getAll(): List<ForegroundTaskInfoDBItem>

	@Query("SELECT * FROM tasks WHERE id LIKE :id LIMIT 1")
	fun getById(id: Int): ForegroundTaskInfoDBItem?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(taskInfo: ForegroundTaskInfoDBItem)

	@Delete
	fun delete(taskInfo: ForegroundTaskInfoDBItem)

	@Query("DELETE FROM tasks")
	fun deleteAll()
}