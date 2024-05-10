package com.esmartdie.EsmartCafeteriaApi.service.user;

import com.esmartdie.EsmartCafeteriaApi.dto.ClientDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.EmployeeDTO;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import com.esmartdie.EsmartCafeteriaApi.model.user.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {


    <T extends User> T saveUser(T user);


    Client createClientFromDTO(ClientDTO clientDTO);

    Employee createEmployeeFromDTO(EmployeeDTO employeeDTO);

    List<User> getUsers();

    Client getClientById(Long id);

    Employee getEmployeeById(Long id);

    Client updateClientFromDTO(Long id, ClientDTO clientDTO);


    Optional<User> getUserByEmail(String email);

    /*
    User getUserById(Long id);

     */


}
