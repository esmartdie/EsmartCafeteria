package com.esmartdie.EsmartCafeteriaApi.service.user;

import com.esmartdie.EsmartCafeteriaApi.dto.*;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.model.user.User;

public interface IUserService {

    <T extends User> T saveUser(T user);

    ClientDTO createClientFromDTO(NewClientDTO clientDTO);

    EmployeeResponseDTO createEmployeeFromDTO(EmployeeDTO employeeDTO);

    Client getClientById(Long id);

    Employee getEmployeeById(Long id);

    ClientDTO updateClientFromDTO(Long id, UpdateClientDTO clientDTO);

}
