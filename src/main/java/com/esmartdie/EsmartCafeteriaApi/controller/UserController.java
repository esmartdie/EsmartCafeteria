package com.esmartdie.EsmartCafeteriaApi.controller;

import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import com.esmartdie.EsmartCafeteriaApi.service.IUserService;
import com.esmartdie.EsmartCafeteriaApi.utils.ResourceNotFoundException;
import com.esmartdie.EsmartCafeteriaApi.utils.UpdateFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController implements IUserController{

    @Autowired
    private IUserService userService;

    @Override
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @Override
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveUser(@RequestBody User user) {
        userService.saveUser(user);
    }

    @Override
    @PostMapping("/users/client/create")
    public ResponseEntity<?> createUser(@RequestBody Client client) {

        Optional<User> existingUser = userService.getUserByEmail(client.getEmail());

        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("The email is already registered");
        }

        userService.saveUser(client);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("The user was created successfully");
    }

    @Override
    @PostMapping("/users/employee/create")
    public ResponseEntity<?> createEmployee(@RequestBody Employee employee) {

        Optional<User> existingUser = userService.getUserByEmail(employee.getEmail());

        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("The email is already registered");
        }

        userService.saveUser(employee);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("The employee was created successfully");
    }

    @Override
    @GetMapping("/users/client/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public User getClientInfo(@PathVariable Long id) {

        Optional<User> existingClientOptional = userService.getUserById(id);

        if (!existingClientOptional.isPresent()) {
            throw new ResourceNotFoundException("Client not found with id: " + id);
        }

        User existingClient = existingClientOptional.get();

        return existingClient;
    }
    @Override
    @PatchMapping("/users/client/{id}/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateClientSoft(@PathVariable Long id, @RequestBody Client updatedClient) {

        try {
            Optional<User> existingClientOptional = userService.getUserById(id);

            if (!existingClientOptional.isPresent()) {
                throw new ResourceNotFoundException("Client not found with id: " + id);
            }

            User existingClient = existingClientOptional.get();

            existingClient.setName(updatedClient.getName());
            existingClient.setLastName(updatedClient.getLastName());

            userService.saveUser(existingClient);
        }catch (Exception e) {
            throw new UpdateFailedException("Error when try to update client with id: " + id, e);
        }
    }


}
