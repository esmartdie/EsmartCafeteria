package com.esmartdie.EsmartCafeteriaApi.controller.user;

import com.esmartdie.EsmartCafeteriaApi.dto.ClientDTO;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface IUserController {

    List<User> getUsers();

    void saveUser(User user);

    @PostMapping("/users/client/create")
    ResponseEntity<String> createUser(@Valid @RequestBody ClientDTO clientDTO);

    ResponseEntity<?> createEmployee(@RequestBody Employee employee);

    User getClientInfo(@PathVariable Long id);

    void updateClientSoft(@PathVariable Long id, @RequestBody Client updatedClient);
}
