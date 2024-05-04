package com.esmartdie.EsmartCafeteriaApi.controller.user;

import com.esmartdie.EsmartCafeteriaApi.dto.RoleToUserDTO;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;

public interface IRoleController {

    void saveRole(Role role);
    void addRoleToUser(RoleToUserDTO roleToUserDTO);
}
