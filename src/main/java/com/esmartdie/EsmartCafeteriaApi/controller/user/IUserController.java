package com.esmartdie.EsmartCafeteriaApi.controller.user;

import com.esmartdie.EsmartCafeteriaApi.dto.ClientDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.EmployeeDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.NewClientDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.UpdateClientDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface IUserController {

    //List<User> getUsers();
   //void saveUser(User user);

    ResponseEntity<?> createClient(@Valid @RequestBody NewClientDTO clientDTO);

    ResponseEntity<?> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO);

    ResponseEntity<ClientDTO> getClientInfo(@PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id);

    ResponseEntity<EmployeeDTO> getEmployeeInfo(@PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id);

    void updateClient(@PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id,
                      @Valid @RequestBody UpdateClientDTO clientDTO);
}
