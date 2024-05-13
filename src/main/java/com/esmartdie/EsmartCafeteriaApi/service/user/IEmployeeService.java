package com.esmartdie.EsmartCafeteriaApi.service.user;

import com.esmartdie.EsmartCafeteriaApi.dto.EmployeeDTO;

import java.util.List;

public interface IEmployeeService {

    List<EmployeeDTO> getActiveEmployees();

    List<EmployeeDTO> getInactiveEmployees();

    void updateEmployeeStatus(Long id, boolean isActive);
}
