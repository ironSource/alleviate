package com.rotemati.foregroundsdk.tests

import androidx.test.core.app.ApplicationProvider
import com.rotemati.foregroundsdk.common.api.test
import com.rotemati.foregroundsdk.external.taskinfo.foregroundTaskInfo
import com.rotemati.foregroundsdk.external.taskinfo.network.NetworkType
import com.rotemati.foregroundsdk.internal.repositories.PendingTasksRepository
import com.rotemati.foregroundsdk.internal.repositories.TaskInfoSpec
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates.notNull

@RunWith(RobolectricTestRunner::class)
internal class RepoTests {

	private val repository = PendingTasksRepository(ApplicationProvider.getApplicationContext())

	@Test
	fun `WHEN adding new task to db THEN the new inserted task id SHOULD first task id`() = test {
		var taskId: Int by notNull()
		var actualValue: Int? = null
		var taskInfoSpec: TaskInfoSpec by notNull()
		arrange {
			// prepare mock data
			taskId = 909192
			val foregroundTaskInfo = foregroundTaskInfo {
				id = taskId
				networkType = NetworkType.Any
				persisted = true
				minLatencyMillis = TimeUnit.SECONDS.toMillis(5)
				timeoutMillis = TimeUnit.SECONDS.toMillis(15)
			}
			taskInfoSpec = TaskInfoSpec(foregroundTaskInfo, javaClass.name)
		}
		act {
			repository.insert(taskInfoSpec)
			actualValue = repository.getTaskInfo(taskId)?.foregroundTaskInfo?.id
		}
		assert { assertThat(actualValue, equalTo(taskId)) }
	}

	@Test
	fun `WHEN adding new task to db THEN delete it the returned task from db SHOULD be null`() =
			test {
				var taskId: Int by notNull()
				var actualValue: TaskInfoSpec? = null
				var taskInfoSpec: TaskInfoSpec by notNull()
				arrange {
					// prepare mock data
					taskId = 909
					val foregroundTaskInfo = foregroundTaskInfo {
						id = taskId
						networkType = NetworkType.Any
						persisted = true
						minLatencyMillis = TimeUnit.SECONDS.toMillis(5)
						timeoutMillis = TimeUnit.SECONDS.toMillis(15)
					}
					taskInfoSpec = TaskInfoSpec(foregroundTaskInfo, "someComponentName")
				}
				act {
					repository.insert(taskInfoSpec)
					repository.delete(taskInfoSpec.foregroundTaskInfo.id)
					actualValue = repository.getTaskInfo(taskInfoSpec.foregroundTaskInfo.id)
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
				var tasksList: List<TaskInfoSpec> by notNull()
				arrange {
					if (repository.getAll().isNotEmpty()) {
						throw IllegalStateException("the db should be empty")
					}
					expectedValue = 3
					// prepare mock data
					tasksList = listOf(
							TaskInfoSpec(foregroundTaskInfo { id = 1 }, "someClassName1"),
							TaskInfoSpec(foregroundTaskInfo { id = 2 }, "someClassName2"),
							TaskInfoSpec(foregroundTaskInfo { id = 3 }, "someClassName3")
					)
				}
				act {
					tasksList.forEach { repository.insert(it) }
					actualValue = repository.getAll().size
				}
				assert { assertThat(actualValue, equalTo(expectedValue)) }
			}
}