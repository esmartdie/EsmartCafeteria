package com.esmartdie.EsmartCafeteriaApi.controller.user;

import com.esmartdie.EsmartCafeteriaApi.dto.*;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.service.user.IUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController implements IUserController{

    @Autowired
    private IUserService userService;

    @Override
    @PostMapping("/signup")
    public ResponseEntity<?> createClient(@Valid @RequestBody NewClientDTO clientDTO) {
        ClientDTO client = userService.createClientFromDTO(clientDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new GenericApiResponseDTO(true, "User created successfully", client));
    }

    @Override
    @PostMapping("/admin/employee/create")
    public ResponseEntity<?> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO employee = userService.createEmployeeFromDTO(employeeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new GenericApiResponseDTO(true, "Employee created successfully", employee));
    }


    @Override
    @GetMapping("/users/client/{id}")
    public ResponseEntity<ClientDTO> getClientInfo(@PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id) {

        Client client = userService.getClientById(id);


        ClientDTO clientDTO = new ClientDTO(
                client.getName(),
                client.getLastName(),
                client.getEmail(),
                client.getActive(),
                client.getRating()
        );

        return ResponseEntity.ok(clientDTO);
    }

    @Override
    @GetMapping("/admin/employee/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeInfo(@PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id) {

        Employee employee = userService.getEmployeeById(id);


        EmployeeDTO employeeDTO = new EmployeeDTO(
                employee.getName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getActive(),
                employee.getEmployee_id()
        );

        return ResponseEntity.ok(employeeDTO);
    }

    @Override
    @PatchMapping("/users/client/{id}/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateClient(@PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id,
                             @Valid @RequestBody UpdateClientDTO clientDTO) {

        userService.updateClientFromDTO(id, clientDTO);
    }


}
