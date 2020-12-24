package com.rotemati.foregroundsdk.logger

interface ForegroundLogger {
	val logsEnabled: Boolean
	val tag: String
	fun v(msg: String)
	fun d(msg: String)
	fun i(msg: String)
	fun w(msg: String)
	fun e(msg: String)
	fun wtf(msg: String)
}

