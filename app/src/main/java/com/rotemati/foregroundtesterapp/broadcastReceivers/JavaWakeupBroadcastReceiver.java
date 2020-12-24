package com.rotemati.foregroundtesterapp.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rotemati.foregroundsdk.foregroundtask.ForegroundTasksScheduler;
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.ForegroundTaskInfo;
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.network.NetworkType;
import com.rotemati.foregroundtesterapp.services.CoroutineRepoForegroundService;

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

		new ForegroundTasksScheduler(context).scheduleForeground(CoroutineRepoForegroundService.class, foregroundTaskInfo);
	}
}
