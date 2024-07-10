package com.eternalcode.plots.scheduler

interface Task {
    fun cancel()

    val isCanceled: Boolean

    val isAsync: Boolean
}
