package com.rotemati.foregroundtesterapp

import com.rotemati.foregroundsdk.foregroundtask.ForegroundObtainer
import com.rotemati.foregroundtesterapp.logger.AppLogger
import kotlinx.coroutines.delay

class ReposForegroundObtainer : ForegroundObtainer {

	override suspend fun onForegroundObtained() {
		AppLogger.logMethod()
		//do some long task
		delay(10000)
	}
}