package com.esmartdie.EsmartCafeteriaApi.controller.user;

import com.esmartdie.EsmartCafeteriaApi.dto.ClientDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.EmployeeDTO;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface IUserController {

    List<User> getUsers();
    void saveUser(User user);

    ResponseEntity<String> createClient(@Valid @RequestBody ClientDTO clientDTO);

    ResponseEntity<String> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO);

    User getClientInfo(@PathVariable Long id);

    void updateClientSoft(@PathVariable Long id, @RequestBody Client updatedClient);
}
