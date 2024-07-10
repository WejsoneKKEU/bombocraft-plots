package com.eternalcode.plots.role

import java.util.*

interface RoleRepository {
    val role: List<Role?>?

    fun findRoleByName(name: String?): Optional<Role?>
}
