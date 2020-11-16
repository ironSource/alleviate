package com.rotemati.foregroundsdk.foregroundtask

import java.io.Serializable

interface ForegroundObtainer : Serializable {
    suspend fun onForegroundObtained()
}