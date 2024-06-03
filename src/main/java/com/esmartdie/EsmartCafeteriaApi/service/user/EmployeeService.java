package com.esmartdie.EsmartCafeteriaApi.service.user;


import com.esmartdie.EsmartCafeteriaApi.dto.EmployeeDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.EmployeeResponseDTO;
import com.esmartdie.EsmartCafeteriaApi.exception.ResourceNotFoundException;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IEmployeeRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import com.esmartdie.EsmartCafeteriaApi.utils.DTOConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService implements IEmployeeService{

    @Autowired
    private IEmployeeRepository employeeRepository;

    @Autowired
    private IUserRepository userRepository;

    private final DTOConverter converter;

    @Override
    public List<EmployeeResponseDTO> getActiveEmployees() {
        log.info("Fetching all active Employees");
        List<Employee> employees = employeeRepository.findAllActive();
        return converter.createEmployeeResponseDTOList(employees);
    }

    @Override
    public List<EmployeeResponseDTO> getInactiveEmployees() {
        log.info("Fetching all inactive Employees");
        List<Employee> employees = employeeRepository.findAllInactive();
        return converter.createEmployeeResponseDTOList(employees);
    }

    @Override
    public void updateEmployeeStatus(Long id, boolean isActive) {

        Employee employee = (Employee) userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        employee.setActive(isActive);
        userRepository.save(employee);
    }

}
