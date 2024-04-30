package com.esmartdie.EsmartCafeteriaApi.controller;

import com.esmartdie.EsmartCafeteriaApi.dto.RoleToUserDTO;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import com.esmartdie.EsmartCafeteriaApi.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RoleController implements IRoleController{


    @Autowired
    private IUserService userService;

    @PostMapping("/roles")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveRole(@RequestBody Role role) {
        userService.saveRole(role);
    }

    @PostMapping("/roles/addtouser")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addRoleToUser(@RequestBody RoleToUserDTO roleToUserDTO) {
        userService.addRoleToUser(roleToUserDTO.getUsername(), roleToUserDTO.getRoleName());
    }
}
