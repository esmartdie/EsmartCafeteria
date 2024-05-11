package com.esmartdie.EsmartCafeteriaApi.service.user;

import com.esmartdie.EsmartCafeteriaApi.dto.EmployeeDTO;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;

import java.util.List;
import java.util.Optional;

public interface IEmployeeService {

    List<EmployeeDTO> getActiveEmployees();

    List<EmployeeDTO> getInactiveEmployees();

    void updateEmployeeStatus(Long id, boolean isActive);
}
