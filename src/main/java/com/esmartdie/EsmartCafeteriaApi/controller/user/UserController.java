package com.esmartdie.EsmartCafeteriaApi.controller.user;

import com.esmartdie.EsmartCafeteriaApi.dto.ClientDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.EmployeeDTO;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import com.esmartdie.EsmartCafeteriaApi.service.user.IUserService;
import com.esmartdie.EsmartCafeteriaApi.exception.ResourceNotFoundException;
import com.esmartdie.EsmartCafeteriaApi.exception.UpdateFailedException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
    public ResponseEntity<String> createClient(@Valid @RequestBody ClientDTO clientDTO) {
        Client client = userService.createClientFromDTO(clientDTO);
        userService.saveUser(client);
        return ResponseEntity.status(HttpStatus.CREATED).body("The user was created successfully");
    }

    @Override
    @PostMapping("/users/employee/create")
    public ResponseEntity<String> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        Employee employee = userService.createEmployeeFromDTO(employeeDTO);
        userService.saveUser(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body("The user was created successfully");
    }


    @Override
    @GetMapping("/users/client/{id}")
    public ResponseEntity<ClientDTO> getClientInfo(@PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id) {

        Client client = userService.getClientById(id);


        ClientDTO clientDTO = new ClientDTO(
                client.getName(),
                client.getLastName(),
                client.getEmail(),
                client.getActive()
        );

        return ResponseEntity.ok(clientDTO);
    }

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */
    @Override
    @PatchMapping("/users/client/{id}/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateClientSoft(@PathVariable Long id, @RequestBody Client updatedClient) {

        try {
            User existingClient = userService.getUserById(id);

            existingClient.setName(updatedClient.getName());
            existingClient.setLastName(updatedClient.getLastName());

            userService.saveUser(existingClient);
        }catch (Exception e) {
            throw new UpdateFailedException("Error when try to update client with id: " + id, e);
        }
    }
    @Override
    @GetMapping("/users/employee/{id}")
    public ResponseEntity<EmployeeDTO> getClientInfo(@PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id) {

        Employee employee = userService.getClientById(id);


        EmployeeDTO employeeDTO = new EmployeeDTO(
                employee.getName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getActive(),
                employee.getEmployee_id()
        );

        return ResponseEntity.ok(employeeDTO);
    }


}
