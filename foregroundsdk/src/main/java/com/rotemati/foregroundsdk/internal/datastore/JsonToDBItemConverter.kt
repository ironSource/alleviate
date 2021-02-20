package com.rotemati.foregroundsdk.internal.datastore

import com.rotemati.foregroundsdk.external.retryepolicy.RetryData
import com.rotemati.foregroundsdk.external.retryepolicy.RetryPolicy
import com.rotemati.foregroundsdk.external.taskinfo.network.NetworkType
import org.json.JSONException
import org.json.JSONObject

private const val ID = "id"
private const val NETWORK_TYPE = "networkType"
private const val PERSISTED = "persisted"
private const val MIN_LATENCY_MILLIS = "minLatencyMillis"
private const val RETRY_COUNT = "retryCount"
private const val TRIGGER_TIME = "triggerTime"
private const val COMPONENT_NAME = "componentName"
private const val TIMEOUT = "timeout"
private const val RETRY_POLICY = "retryPolicy"
private const val RETRY_INITIAL_BACKOFF = "retryInitialBackoff"

internal class JsonToDBItemConverter {

	fun toJson(foregroundTaskInfoDBItem: ForegroundTaskInfoDBItem): JSONObject {
		return JSONObject().apply {
			put(ID, foregroundTaskInfoDBItem.id)
			put(NETWORK_TYPE, foregroundTaskInfoDBItem.networkType.name)
			put(PERSISTED, foregroundTaskInfoDBItem.persisted)
			put(MIN_LATENCY_MILLIS, foregroundTaskInfoDBItem.minLatencyMillis)
			put(RETRY_COUNT, foregroundTaskInfoDBItem.retryCount)
			put(TRIGGER_TIME, foregroundTaskInfoDBItem.triggerTime)
			put(COMPONENT_NAME, foregroundTaskInfoDBItem.componentName)
			put(TIMEOUT, foregroundTaskInfoDBItem.timeoutMillis)
			put(RETRY_POLICY, foregroundTaskInfoDBItem.retryData.retryPolicy.name)
			put(RETRY_INITIAL_BACKOFF, foregroundTaskInfoDBItem.retryData.initialBackoff)
		}
	}

	fun fromJson(jsonObject: JSONObject): ForegroundTaskInfoDBItem? {
		return try {
			ForegroundTaskInfoDBItem(
					id = jsonObject.getInt(ID),
					networkType = NetworkType.valueOf(jsonObject.getString(NETWORK_TYPE)),
					persisted = jsonObject.getBoolean(PERSISTED),
					minLatencyMillis = jsonObject.getLong(MIN_LATENCY_MILLIS),
					retryCount = jsonObject.getInt(RETRY_COUNT),
					triggerTime = jsonObject.getLong(TRIGGER_TIME),
					componentName = jsonObject.getString(COMPONENT_NAME),
					timeoutMillis = jsonObject.getLong(TIMEOUT),
					retryData = getRetryData(jsonObject)
			)
		} catch (e: JSONException) {
			null
		}
	}

	private fun getRetryData(jsonObject: JSONObject): RetryData {
		return RetryData(
				retryPolicy = RetryPolicy.valueOf(jsonObject.getString(RETRY_POLICY)),
				initialBackoff = jsonObject.getLong(RETRY_INITIAL_BACKOFF)
		)
	}
}