package com.rotemati.foregroundsdk.foreground

import java.io.Serializable

interface ForegroundObtainer : Serializable {
    suspend fun onForegroundObtained()
}