package com.rotemati.foregroundsdk.foregroundtask.internal.extensions

import java.text.SimpleDateFormat
import java.util.*

const val DATE_FORMAT = "dd MMM yyyy HH:mm:ss:SSS"

internal inline fun Long.toDateFormat(): String = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(this)