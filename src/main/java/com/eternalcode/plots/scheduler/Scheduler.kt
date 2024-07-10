package com.eternalcode.plots.scheduler

import panda.std.reactive.Completable
import java.time.Duration
import java.util.function.Supplier

interface Scheduler {
    fun sync(task: Runnable?): Task

    fun async(task: Runnable?): Task

    fun laterSync(task: Runnable?, delay: Duration): Task

    fun laterAsync(task: Runnable?, delay: Duration): Task

    fun timerSync(task: Runnable?, delay: Duration, period: Duration): Task

    fun timerAsync(task: Runnable?, delay: Duration, period: Duration): Task

    fun <T> completeSync(task: Supplier<T>): Completable<T>

    fun <T> completeAsync(task: Supplier<T>): Completable<T>
}
