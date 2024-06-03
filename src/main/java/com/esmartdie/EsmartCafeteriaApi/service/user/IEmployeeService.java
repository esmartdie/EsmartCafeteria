package com.esmartdie.EsmartCafeteriaApi.service.user;

import com.esmartdie.EsmartCafeteriaApi.dto.EmployeeDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.EmployeeResponseDTO;

import java.util.List;

public interface IEmployeeService {

    List<EmployeeResponseDTO> getActiveEmployees();
    List<EmployeeResponseDTO> getInactiveEmployees();
    void updateEmployeeStatus(Long id, boolean isActive);
}
