package com.rotemati.foregroundtesterapp

import com.rotemati.foregroundsdk.foreground.ForegroundObtainer
import com.rotemati.foregroundtesterapp.logger.AppLogger
import kotlinx.coroutines.delay

class ReposForegroundObtainer : ForegroundObtainer {

    override suspend fun onForegroundObtained() {
        AppLogger.logMethod()
        delay(10000)
    }
}