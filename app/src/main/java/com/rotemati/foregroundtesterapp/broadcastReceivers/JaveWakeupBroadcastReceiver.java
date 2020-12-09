package com.rotemati.foregroundtesterapp.broadcastReceivers;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rotemati.foregroundsdk.foregroundtask.ForegroundTasksScheduler;
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.ForegroundTaskInfo;
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.network.NetworkType;
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.retry.RetryPolicy;
import com.rotemati.foregroundtesterapp.services.ReposForegroundService;

import java.util.concurrent.TimeUnit;

import androidx.core.app.NotificationCompat;

class JaveWakeupBroadcastReceiver
		extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		final Notification notification = new NotificationCompat.Builder(context, "java channel").setContentTitle("Java Rotem")
		                                                                                         .setContentText("Java Matityahu")
		                                                                                         .setSmallIcon(android.R.drawable.star_on)
		                                                                                         .build();

		final ForegroundTaskInfo foregroundTaskInfo = new ForegroundTaskInfo.Builder().id(12345)
		                                                                              .networkType(NetworkType.Any)
		                                                                              .persisted(true)
		                                                                              .minLatencyMillis(TimeUnit.SECONDS.toMillis(10))
		                                                                              .timeoutMillis(TimeUnit.SECONDS.toMillis(15))
		                                                                              .notification(notification)
		                                                                              .retryPolicy(new RetryPolicy.Retry(true, 3))
		                                                                              .build();

		new ForegroundTasksScheduler(context).scheduleForeground(ReposForegroundService.class, foregroundTaskInfo);
	}
}
