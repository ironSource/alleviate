package com.rotemati.foregroundsdk.db.tests

import com.rotemati.foregroundsdk.common.api.test
import com.rotemati.foregroundsdk.db.rules.DatabaseRule
import com.rotemati.foregroundsdk.external.taskinfo.foregroundTaskInfo
import com.rotemati.foregroundsdk.external.taskinfo.network.NetworkType
import com.rotemati.foregroundsdk.internal.db.ForegroundTaskInfoDBItem
import com.rotemati.foregroundsdk.internal.db.TaskToDBItemConvertor
import com.rotemati.foregroundsdk.internal.repositories.TaskInfoSpec
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates.notNull

@RunWith(RobolectricTestRunner::class)
internal class DatabaseTests {

	@get:Rule val db = DatabaseRule()

	private val dbItemConvertor = TaskToDBItemConvertor()

	@Test
	fun `WHEN adding new task to db THEN the new inserted task id SHOULD first task id`() = test {
		var taskId: Int by notNull()
		var actualValue: Int? = null
		var dbItem: ForegroundTaskInfoDBItem by notNull()
		arrange {
			// prepare mock data
			taskId = 909192
			val foregroundTaskInfo = foregroundTaskInfo(taskId) {
				networkType = NetworkType.Any
				persisted = true
				minLatencyMillis = TimeUnit.SECONDS.toMillis(5)
				timeoutMillis = TimeUnit.SECONDS.toMillis(15)
			}
			val taskInfoSpec = TaskInfoSpec(foregroundTaskInfo, javaClass.name)
			dbItem = TaskToDBItemConvertor().toDBItem(
					taskInfoSpec.foregroundTaskInfo,
					taskInfoSpec.componentName
			)
		}
		act {
			db.tasksDao.insert(dbItem)
			actualValue = db.tasksDao.getById(taskId)?.id
		}
		assert { assertThat(actualValue, equalTo(taskId)) }
	}

	@Test
	fun `WHEN adding new task to db THEN delete it the returned task from db SHOULD be null`() =
			test {
				var taskId: Int by notNull()
				var actualValue: ForegroundTaskInfoDBItem? = null
				var dbItem: ForegroundTaskInfoDBItem by notNull()
				arrange {
					// prepare mock data
					taskId = 909
					val foregroundTaskInfo = foregroundTaskInfo(taskId) {
						networkType = NetworkType.Any
						persisted = true
						minLatencyMillis = TimeUnit.SECONDS.toMillis(5)
						timeoutMillis = TimeUnit.SECONDS.toMillis(15)
					}
					val taskInfoSpec = TaskInfoSpec(foregroundTaskInfo, "someComponentName")
					dbItem = dbItemConvertor.toDBItem(
							taskInfoSpec.foregroundTaskInfo,
							taskInfoSpec.componentName
					)
				}
				act {
					db.tasksDao.insert(dbItem)
					db.tasksDao.delete(dbItem)
					actualValue = db.tasksDao.getById(taskId)
				}
				assert {
					assertThat(actualValue, equalTo(null))
				}
			}

	@Test
	@Throws(IllegalStateException::class)
	fun `WHEN db is empty and adding X new tasks to the db THEN the size of the returned list SHOULD be X`() =
			test {
				var expectedValue: Int by notNull()
				var actualValue: Int by notNull()
				var tasksList: List<ForegroundTaskInfoDBItem> by notNull()
				arrange {
					if (db.tasksDao.getAll().isNotEmpty()) {
						throw IllegalStateException("the db should be empty")
					}
					expectedValue = 3
					// prepare mock data
					tasksList = listOf(
							dbItemConvertor.toDBItem(foregroundTaskInfo(10), "someClassName1"),
							dbItemConvertor.toDBItem(foregroundTaskInfo(11), "someClassName2"),
							dbItemConvertor.toDBItem(foregroundTaskInfo(21), "someClassName3")
					)
				}
				act {
					tasksList.forEach { db.tasksDao.insert(it) }
					actualValue = db.tasksDao.getAll().size
				}
				assert { assertThat(actualValue, equalTo(expectedValue)) }
			}

}