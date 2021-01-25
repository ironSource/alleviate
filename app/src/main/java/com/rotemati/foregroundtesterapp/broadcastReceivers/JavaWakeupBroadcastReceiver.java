package com.rotemati.foregroundtesterapp.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rotemati.foregroundsdk.external.scheduler.ForegroundTasksSchedulerWrapper;
import com.rotemati.foregroundsdk.external.taskinfo.ForegroundTaskInfo;
import com.rotemati.foregroundsdk.external.taskinfo.network.NetworkType;
import com.rotemati.foregroundtesterapp.services.ReposForegroundService;

import java.util.concurrent.TimeUnit;

public class JavaWakeupBroadcastReceiver
		extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		final ForegroundTaskInfo foregroundTaskInfo = new ForegroundTaskInfo.Builder().id(12345)
		                                                                              .networkType(NetworkType.Any)
		                                                                              .persisted(true)
		                                                                              .minLatencyMillis(TimeUnit.SECONDS.toMillis(10))
		                                                                              .timeoutMillis(TimeUnit.SECONDS.toMillis(15))
		                                                                              .build();

		new ForegroundTasksSchedulerWrapper().scheduleForegroundTask(ReposForegroundService.class, foregroundTaskInfo);
	}
}
