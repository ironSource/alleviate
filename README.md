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
- Kotlin
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
- Java
```java
public void scheduleForegroundTask() {
	final ForegroundTaskInfo foregroundTaskInfo = new ForegroundTaskInfo.Builder().id(12345)
	                                                                              .networkType(NetworkType.Any)
	                                                                              .persisted(true)
	                                                                              .minLatencyMillis(TimeUnit.HOURS.toMillis(12))
	                                                                              .timeoutMillis(TimeUnit.MINUTES.toMillis(1))
	                                                                              .build();

	new ForegroundTasksSchedulerWrapper().scheduleForegroundTask(ReposForegroundService.class, foregroundTaskInfo);
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

## Create Foreground
```kotlin
class ReposForegroundService : ForegroundTaskService() {

    override fun getNotification(): Notification {
        // create channel
        val channelId = resources.getString(R.string.my_channel)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.setSound(null, null)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(resources.getString(R.string.my_title))
            .setContentText(resources.getString(R.string.my_body))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

    override fun doWork(): Result {
        return try {
            val futureRepos = GitHubRepo(getNetworkService()).getRepos()
            Result.Success
        } catch (e: Exception) {
            if (foregroundTaskInfo.retryCount >= 3) {
                Result.Failed
            } else {
                Result.Reschedule(RetryPolicy.Linear)
            }
        }
    }

    override fun onTimeout(): Result {
        return if (foregroundTaskInfo.retryCount >= 3) {
            Result.Failed
        } else {
            Result.Reschedule(RetryPolicy.Exponential)
        }
    }
}
```
