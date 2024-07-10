package com.eternalcode.plots.user

import panda.std.reactive.Completable
import java.util.*
import java.util.concurrent.CompletableFuture

interface UserRepository {
    val users: Completable<List<User?>?>?

    fun getUser(uuid: UUID?): CompletableFuture<User?>?

    fun saveUser(user: User?)
}
