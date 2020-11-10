package com.rotemati.foregroundsdk.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rotemati.foregroundsdk.extensions.scheduleForeground
import com.rotemati.foregroundsdk.jobinfo.PendingJobsRepository
import com.rotemati.foregroundsdk.logger.SDKLogger

class BootCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        SDKLogger.logMethod()
        val pendingJobsRepository = PendingJobsRepository(context)
        val persistJobs = pendingJobsRepository.pendingForegroundJobs.filter { it.persisted }
        persistJobs.forEach {
            scheduleForeground(context, it)
        }
    }
}