# network-exposure

A lightwate Android library that wraps your work requests and ensure they will run approximetly at the time you want it to.
This is done using a component called ForegroundWork that will be triggered when all work constaints are satisfied.

Check out my Medium post for clearer explentation about the problems this library tries to solve:
https://medium.com/@rotemmatityahu/workmanager-does-it-always-manage-to-work-fd8518655052

## Features:
- Easy foreground tasks scheduling.
- In case the app is in 'never' bucket, raise foreground notification in order to get out of the bucket.
- Friendly API using DSLs and Java builder.
- Support custom logger.

## Usage:
```kotlin
fun scheduleForegroundTask() {
	val foregroundTaskInfo = foregroundTaskInfo(11200) {
		networkType = NetworkType.Any
		persisted = true
		minLatencyMillis = TimeUnit.HOURS.toMillis(12)
		timeoutMillis = TimeUnit.MINUTES.toMillis(1)
	}
	ForegroundTasksSchedulerWrapper().scheduleForegroundTask(
			ReposForegroundService::class.java,
			foregroundTaskInfo
	)
}
```

## Initializing the SDK
```kotlin
class MainApplication : Application() {
	override fun onCreate() {
		super.onCreate()
		ForegroundSDK.context = this
		ForegroundSDK.foregroundLogger = CustomAppLogger() // optional
	}
}
```

## Supported foreground task descriptors
```kotlin
val id: Int, // mandatory
val networkType: NetworkType,
val persisted: Boolean,
val minLatencyMillis: Long, 
val timeoutMillis: Long
```


