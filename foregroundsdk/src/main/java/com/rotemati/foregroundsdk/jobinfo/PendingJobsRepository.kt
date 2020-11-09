package com.rotemati.foregroundsdk.jobinfo

import android.content.Context
import com.rotemati.foregroundsdk.sharedprefs.SharedPreferencesProxy

private const val PREFS_FILE = "com.rotemati.JOB_REPO_FILE"

class PendingJobsRepository(context: Context) {

    private val sharedPrefs = SharedPreferencesProxy(context, PREFS_FILE)

    val pendingForegroundJobs: List<ForegroundJobInfo>
        get() = sharedPrefs.all().keys.mapNotNull {
            sharedPrefs.getSharedPrefObject<ForegroundJobInfo>(it, ForegroundJobInfo::class.java)
        }

    fun remove(id: Int) {
        sharedPrefs.remove(id.toString())
    }

    fun save(pendingForegroundJob: ForegroundJobInfo) {
        sharedPrefs.setSharedPrefObject(pendingForegroundJob.id.toString(), pendingForegroundJob)
    }

    fun contains(id: Int): Boolean {
        return pendingForegroundJobs.any { it.id == id }
    }

    fun updateRetryCount(id: Int, retry: Int) {

    }

    fun clear() = sharedPrefs.clear()
}