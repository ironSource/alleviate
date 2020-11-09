package com.rotemati.foregroundtesterapp.logger

import android.util.Log

private const val TAG = "FOREGROUND_TESTER_APP"

class AppLogger {

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