package com.eternalcode.plots.role

import com.eternalcode.plots.member.Member

class RoleService(
    private val roleRepository: RoleRepository,
    private val permissionRepository: RolePermissionRepository
) {
    fun hasPermission(member: Member, vararg permissions: RolePermission?): Boolean {
        val role = this.getRole(member.role)

        for (permission in permissions) {
            if (!permissionRepository.hasRolePermission(role, permission)) {
                return false
            }
        }

        return true
    }

    fun getPermissions(member: Member): Set<RolePermission?>? {
        val role = this.getRole(member.role)

        return this.getPermissions(role)
    }

    private fun getRole(name: String): Role? {
        return roleRepository.findRoleByName(name).orElse(Role.Companion.NONE)
    }

    private fun getPermissions(role: Role?): Set<RolePermission?>? {
        return permissionRepository.findAllByRank(role)
    }
}
