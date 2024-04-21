package com.esmartdie.EsmartCafeteriaApi.service;

import com.esmartdie.EsmartCafeteriaApi.dto.RoleToUserDTO;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;

public interface IRoleService {

    void saveRole(Role role);

    void addRoleToUser(RoleToUserDTO roleToUserDTO);
}
