package com.eternalcode.plots.role

@JvmRecord
data class Role(val name: String) {
    companion object {
        val NONE: Role = Role("__NONE__")
    }
}
