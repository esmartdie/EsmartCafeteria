package com.esmartdie.EsmartCafeteriaApi.service.user;

import com.esmartdie.EsmartCafeteriaApi.dto.ClientDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.EmployeeDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.NewClientDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.UpdateClientDTO;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.model.user.User;

public interface IUserService {


    <T extends User> T saveUser(T user);


    ClientDTO createClientFromDTO(NewClientDTO clientDTO);

    EmployeeDTO createEmployeeFromDTO(EmployeeDTO employeeDTO);

    // List<User> getUsers();

    Client getClientById(Long id);

    Employee getEmployeeById(Long id);

    ClientDTO updateClientFromDTO(Long id, UpdateClientDTO clientDTO);


    //Optional<User> getUserByEmail(String email);


  //  User getUserById(Long id);




}
