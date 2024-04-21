package com.esmartdie.EsmartCafeteriaApi.controller;

import com.esmartdie.EsmartCafeteriaApi.model.user.User;

import java.util.List;

public interface IUserController {

    List<User> getUsers();

    User saveUser(User user);
}
