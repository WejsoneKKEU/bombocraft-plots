package com.eternalcode.plots.role

interface RolePermissionRepository {
    fun findAllByRank(role: Role?): Set<RolePermission?>?

    fun hasRolePermission(role: Role?, permission: RolePermission?): Boolean
}
