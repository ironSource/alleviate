package com.rotemati.foregroundsdk.bucketPoller.tests

import com.rotemati.foregroundsdk.bucketPoller.mocks.MockBucketPollingStrategy
import com.rotemati.foregroundsdk.common.api.test
import com.rotemati.foregroundsdk.foregroundtask.external.bucketpolling.BucketPollingData
import com.rotemati.foregroundsdk.foregroundtask.internal.bucketpolling.BucketPoller
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates.notNull

@RunWith(RobolectricTestRunner::class)
class BucketPollerTests {

	@Test
	fun `WHEN bucket is 'never' THEN polling value should be 50`() = test {
		var actualValue: Int by notNull()
		arrange {
			BucketPoller.strategy = MockBucketPollingStrategy()
			BucketPoller.pollingData = BucketPollingData(
					interval = TimeUnit.SECONDS.toMillis(5),
					timeout = TimeUnit.SECONDS.toMillis(20)
			)
		}
		act {
			actualValue = BucketPoller.bucket
		}
		assert {
			Assert.assertThat(actualValue, equalTo(50))
		}
	}

	@Test
	fun `WHEN bucket is 'never' and changed to 'rare' THEN polling should stop`() = test {
//		var bucketPoller: BucketPoller by notNull()
//		val pollingDelay = TimeUnit.SECONDS.toMillis(5000)
//		val mockBucketPollingStrategy = MockBucketPollingStrategy()
//		var startTime: Long by notNull()
//		var endTime: Long by notNull()
//
//		bucketPoller = BucketPoller(pollingDelay, mockBucketPollingStrategy)
//		bucketPoller.onNoLongerNeeded = {
//			println("onNoLongerNeeded")
//			endTime = System.currentTimeMillis()
//			Assert.assertNotEquals(startTime, endTime)
//		}
//		startTime = System.currentTimeMillis()
//		println("start")
//		bucketPoller.start()
//		mockBucketPollingStrategy.mockValue = 40
	}
}