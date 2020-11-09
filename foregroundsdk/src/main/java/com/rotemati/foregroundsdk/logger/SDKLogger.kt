package com.rotemati.foregroundsdk.logger

import android.content.Context
import android.util.Log
import java.io.IOException
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat


private const val TAG = "FOREGROUND_SDK"

internal class SDKLogger {

	companion object {
		fun v(msg: String) {
			doLog(Log.VERBOSE, msg)
		}

		fun d(msg: String) {
			doLog(Log.DEBUG, msg)
		}

		fun i(msg: String) {
			doLog(Log.INFO, msg)
		}

		fun w(msg: String) {
			doLog(Log.WARN, msg)
		}

		fun e(msg: String) {
			doLog(Log.ERROR, msg)
		}

		fun wtf(msg: String) {
			doLog(Log.ASSERT, msg)
		}

		fun logMethod() {
			doLog(Log.VERBOSE, "called")
		}

		fun logToFile(context: Context, msg: String) {
			var detailedMsg = msg
			val stackTrace = Thread.currentThread().stackTrace
			if (stackTrace != null && stackTrace.size > 4) {
				val element = stackTrace[4]
				val fullClassName = element.className
				val className =
						fullClassName.substring(fullClassName.lastIndexOf(".") + 1) // no package
				val threadName = Thread.currentThread().name

				//add class and method data to logText
				val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z")

				detailedMsg =
						dateFormat.format(System.currentTimeMillis()) + "T:" + threadName + " | " + className + " , " + element.methodName + "() | " + msg + "\n"
			}
			try {
				val outputStreamWriter = OutputStreamWriter(context.openFileOutput("logs.txt", Context.MODE_APPEND))
				outputStreamWriter.append(detailedMsg)
				outputStreamWriter.close()
			} catch (e: IOException) {
				e("File write failed: $e")
			}
		}

		private fun doLog(logLevel: Int, msg: String) {
			var detailedMsg = msg
			val stackTrace = Thread.currentThread().stackTrace
			if (stackTrace != null && stackTrace.size > 4) {
				val element = stackTrace[4]
				val fullClassName = element.className
				val className =
						fullClassName.substring(fullClassName.lastIndexOf(".") + 1) // no package
				val threadName = Thread.currentThread()
						.name

				//add class and method data to logText
				detailedMsg =
						"T:" + threadName + " | " + className + " , " + element.methodName + "() | " + msg
			}
			if (true) {
				Log.println(logLevel, TAG, detailedMsg)
			}
		}
	}
}