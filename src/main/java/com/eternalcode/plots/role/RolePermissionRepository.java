package com.eternalcode.plots.role;

import java.util.List;
import java.util.Set;

public interface RolePermissionRepository {

    Set<RolePermission> findAllByRank(Role role);

    boolean hasRolePermission(Role role, RolePermission permission);

}
