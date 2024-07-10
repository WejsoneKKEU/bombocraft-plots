package com.eternalcode.plots.scheduler

import org.bukkit.plugin.Plugin
import panda.std.reactive.Completable
import java.time.Duration
import java.util.function.Supplier

class BukkitSchedulerImpl(private val plugin: Plugin) : Scheduler {
    private val rootScheduler = plugin.server.scheduler

    override fun sync(task: Runnable?): Task {
        return BukkitTaskImpl(rootScheduler.runTask(this.plugin, task!!))
    }

    override fun async(task: Runnable?): Task {
        return BukkitTaskImpl(rootScheduler.runTaskAsynchronously(this.plugin, task!!))
    }

    override fun laterSync(task: Runnable?, delay: Duration): Task {
        return BukkitTaskImpl(rootScheduler.runTaskLater(this.plugin, task!!, this.toTick(delay)))
    }

    override fun laterAsync(task: Runnable?, delay: Duration): Task {
        return BukkitTaskImpl(rootScheduler.runTaskLaterAsynchronously(this.plugin, task!!, this.toTick(delay)))
    }

    override fun timerSync(task: Runnable?, delay: Duration, period: Duration): Task {
        return BukkitTaskImpl(rootScheduler.runTaskTimer(this.plugin, task!!, this.toTick(delay), this.toTick(period)))
    }

    override fun timerAsync(task: Runnable?, delay: Duration, period: Duration): Task {
        return BukkitTaskImpl(
            rootScheduler.runTaskTimerAsynchronously(
                this.plugin,
                task!!,
                this.toTick(delay),
                this.toTick(period)
            )
        )
    }

    override fun <T> completeSync(task: Supplier<T>): Completable<T> {
        val completable = Completable<T>()
        rootScheduler.runTask(this.plugin, Runnable { completable.complete(task.get()) })
        return completable
    }

    override fun <T> completeAsync(task: Supplier<T>): Completable<T> {
        val completable = Completable<T>()
        rootScheduler.runTaskAsynchronously(this.plugin, Runnable { completable.complete(task.get()) })
        return completable
    }

    private fun toTick(duration: Duration): Long {
        return duration.toMillis() / 50L
    }
}
