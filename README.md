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
val networkType: NetworkType
val persisted: Boolean
val minLatencyMillis: Long 
val timeoutMillis: Long
```

## Create foreground task
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
## Download
```groovy
dependencies {
    implementation 'com.ironsource.aura.blabla:blabla:1.0.0'
}
```
## License
Copyright (c) 2021 Rotem Matityahu

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
