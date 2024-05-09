package com.esmartdie.EsmartCafeteriaApi.service.user;

import com.esmartdie.EsmartCafeteriaApi.dto.RoleToUserDTO;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;

public interface IRoleService {

    Role saveRole(Role role);

    void addRoleToUser(String username, String roleName);
}
