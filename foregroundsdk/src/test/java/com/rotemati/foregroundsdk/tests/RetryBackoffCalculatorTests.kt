package com.rotemati.foregroundsdk.tests

import com.rotemati.foregroundsdk.common.api.test
import com.rotemati.foregroundsdk.external.retryepolicy.RetryData
import com.rotemati.foregroundsdk.external.retryepolicy.RetryPolicy
import com.rotemati.foregroundsdk.internal.backoff.RetryBackoffCalculator
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert
import org.junit.Test
import kotlin.properties.Delegates.notNull

class RetryBackoffCalculatorTests {
	private val calculator = RetryBackoffCalculator()

	@Test
	fun `WHEN calculating Linear backoff THEN the backoff SHOULD equal 6000`() = test {
		var retryData: RetryData by notNull()
		var retryCount: Int by notNull()
		var actualValue: Long by notNull()
		var expectedValue: Long by notNull()
		arrange {
			retryData = RetryData(RetryPolicy.Linear, 2000)
			retryCount = 3
			expectedValue = 6000
		}
		act {
			actualValue = calculator.calculate(retryData, retryCount)
		}
		assert {
			Assert.assertThat(actualValue, equalTo(expectedValue))
		}
	}

	@Test
	fun `WHEN calculating Exponential backoff THEN the backoff SHOULD equal 16000000`() = test {
		var retryData: RetryData by notNull()
		var retryCount: Int by notNull()
		var actualValue: Long by notNull()
		var expectedValue: Long by notNull()
		arrange {
			retryData = RetryData(RetryPolicy.Exponential, 2000)
			retryCount = 3
			expectedValue = 16000000
			print("expectedValue: $expectedValue")
		}
		act {
			actualValue = calculator.calculate(retryData, retryCount)
		}
		assert {
			Assert.assertThat(actualValue, equalTo(expectedValue))
		}
	}
}