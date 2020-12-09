package com.rotemati.foregroundtesterapp.services

import com.rotemati.foregroundsdk.foregroundtask.ForegroundTaskService
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.result.Result
import com.rotemati.foregroundtesterapp.logger.AppLogger
import com.rotemati.foregroundtesterapp.model.GitHubRepo
import com.rotemati.foregroundtesterapp.webservices.getNetworkService

class ReposForegroundService : ForegroundTaskService() {
	override fun doWork(): Result {
		AppLogger.logMethod()
		return try {
			val futureRepos = GitHubRepo(getNetworkService()).getRepos()
			AppLogger.i("${futureRepos.get().size} repos fetched")
			Result.Success
		} catch (e: Exception) {
			e.message?.let { AppLogger.e(it) }
			Result.Reschedule
		}
	}
}