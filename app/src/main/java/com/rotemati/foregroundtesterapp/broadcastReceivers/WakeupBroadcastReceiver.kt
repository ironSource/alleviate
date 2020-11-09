package com.rotemati.foregroundtesterapp.broadcastReceivers

import android.app.job.JobInfo.NETWORK_TYPE_ANY
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rotemati.foregroundsdk.NotificationBuilder
import com.rotemati.foregroundsdk.extensions.scheduleForeground
import com.rotemati.foregroundsdk.jobinfo.ForegroundJobInfo
import com.rotemati.foregroundtesterapp.logger.AppLogger
import com.rotemati.foregroundtesterapp.model.GitHubRepo
import com.rotemati.foregroundtesterapp.webservices.getNetworkService
import java.util.concurrent.TimeUnit

class WakeupBroadcastReceiver : BroadcastReceiver() {
    private val repository = GitHubRepo(getNetworkService())

    override fun onReceive(context: Context, intent: Intent?) {
//        val sdkInitializer = sdkInitializer {
//            context { context }
//            bucketPollingDelay { TimeUnit.SECONDS.toMillis(3) }
//            bucketPollingTimeout { TimeUnit.SECONDS.toMillis(30) }
//            notification {
//                channel { context.getString(R.string.my_channel) }
//                title { context.getString(R.string.my_title) }
//                body { context.getString(R.string.my_body) }
//                iconRes { R.drawable.ic_launcher_foreground }
//            }
//        }
//        GlobalScope.launch {
//            sdkInitializer.start()
//            try {
//                delay(10000)
//                val repos = repository.getRepos()
//                AppLogger.i("${repos.size} repos fetched")
//            } catch (exception: Exception) {
//                exception.message?.let { AppLogger.e(it) }
//            } finally {
//                AppLogger.i("job finished!")
//                sdkInitializer.finish()
//            }
//        }
        AppLogger.logMethod()
        val notification = NotificationBuilder(context).build()
        val foregroundJobInfo = ForegroundJobInfo(
            id = 11200,
            minLatencyMillis = TimeUnit.SECONDS.toMillis(0),
            isPersisted = true,
            networkType = NETWORK_TYPE_ANY,
            notification = notification,
            timeout = 10000,
//            foregroundObtainer = ReposForegroundObtainer(),
            rescheduleOnFail = true,
            maxRetries = 3
        )

        scheduleForeground(context, foregroundJobInfo)
    }
}