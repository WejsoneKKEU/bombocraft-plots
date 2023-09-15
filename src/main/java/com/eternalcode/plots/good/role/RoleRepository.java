package com.eternalcode.plots.good.role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {

    List<Role> getRole();

    Optional<Role> findRoleByName(String name);

}