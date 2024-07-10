package com.eternalcode.plots.scheduler

import org.bukkit.scheduler.BukkitTask

internal class BukkitTaskImpl(private val rootTask: BukkitTask) : Task {
    override fun cancel() {
        rootTask.cancel()
    }

    override val isCanceled: Boolean
        get() = rootTask.isCancelled

    override val isAsync: Boolean
        get() = !rootTask.isSync
}
