package com.esmartdie.EsmartCafeteriaApi.service;

import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;

import java.util.List;
import java.util.Optional;

public interface IEmployeeService {

    Optional<List<Employee>> getActiveEmployees();

    Optional<List<Employee>> getInactiveEmployees();

    void activateEmployee(Long employeeId);

    void deactivateEmployee(Long employeeId);

    void deleteEmployee(Long employeeId);

}
