package com.esmartdie.EsmartCafeteriaApi.controller;

import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface IUserController {

    List<User> getUsers();

    void saveUser(User user);

    ResponseEntity<?> createUser(@RequestBody Client client);


    ResponseEntity<?> createEmployee(@RequestBody Employee employee);

    User getClientInfo(@PathVariable Long id);

    void updateClientSoft(@PathVariable Long id, @RequestBody Client updatedClient);
}
