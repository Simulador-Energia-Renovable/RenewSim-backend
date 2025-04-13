package com.renewsim.backend.role;

import java.util.List;
import java.util.Set;

public interface RoleService {

    Role getRoleByName(RoleName roleName);

    Set<Role> getRolesFromStrings(Set<String> roleNames);

    Set<Role> getRolesByNames(List<String> roleNames);
}


