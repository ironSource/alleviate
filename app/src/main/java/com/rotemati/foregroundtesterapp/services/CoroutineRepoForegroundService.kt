package com.rotemati.foregroundtesterapp.services

import com.rotemati.foregroundsdk.foregroundtask.CoroutineForegroundTaskService
import com.rotemati.foregroundsdk.foregroundtask.taskinfo.result.Result
import com.rotemati.foregroundtesterapp.logger.AppLogger
import com.rotemati.foregroundtesterapp.model.GitHubRepo
import com.rotemati.foregroundtesterapp.webservices.getNetworkService

class CoroutineRepoForegroundService : CoroutineForegroundTaskService() {

	override suspend fun doWork(): Result {
		AppLogger.logMethod()
		return try {
			val repos = GitHubRepo(getNetworkService()).getReposSuspend()
			AppLogger.i("${repos.size} repos fetched")
			Result.Success
		} catch (e: Exception) {
			e.message?.let { AppLogger.e(it) }
			Result.Failed(e)
		}
	}
}