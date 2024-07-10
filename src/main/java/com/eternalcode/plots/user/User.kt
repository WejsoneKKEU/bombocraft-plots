package com.eternalcode.plots.user

import java.util.*

class User internal constructor(private val uuid: UUID) {
    private var name: String? = null

    fun uuid(): UUID {
        return this.uuid
    }

    fun name(): String? {
        return this.name
    }

    fun updateName(name: String?) {
        this.name = name
    }
}
