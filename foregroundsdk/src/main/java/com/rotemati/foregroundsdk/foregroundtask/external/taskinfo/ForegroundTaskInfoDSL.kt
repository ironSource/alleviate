package com.rotemati.foregroundsdk.foregroundtask.external.taskinfo

import com.rotemati.foregroundsdk.foregroundtask.external.taskinfo.network.NetworkType

class ForegroundTaskInfoDSL {

    var id: Int = -1
    var networkType: NetworkType = NetworkType.None
    var persisted: Boolean = false
    var minLatencyMillis: Long = 0
    var timeoutMillis: Long = Long.MAX_VALUE

    fun build(id: Int): ForegroundTaskInfo {
        return ForegroundTaskInfo(id, networkType, persisted, minLatencyMillis, timeoutMillis)
    }
}

fun foregroundTaskInfo(id: Int, block: ForegroundTaskInfoDSL.() -> Unit = {}): ForegroundTaskInfo {
    return ForegroundTaskInfoDSL().apply(block).build(id)
}