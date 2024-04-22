package com.esmartdie.EsmartCafeteriaApi.service;

import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import com.esmartdie.EsmartCafeteriaApi.model.user.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    User saveUser(User user);

    Role saveRole(Role role);

    void addRoleToUser(String username, String roleName);

    User getUser(String username);

    List<User> getUsers();

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserById(Long id);
}
