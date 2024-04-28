package com.esmartdie.EsmartCafeteriaApi.service.user;


import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IEmployeeRepository;
import com.esmartdie.EsmartCafeteriaApi.utils.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService implements IEmployeeService{

    @Autowired
    private IEmployeeRepository employeeRepository;

    @Override
    public Optional<List<Employee>> getActiveEmployees() {
        log.info("Fetching all active Employees");
        return employeeRepository.findAllActive();
    }

    @Override
    public Optional<List<Employee>> getInactiveEmployees() {
        log.info("Fetching all inactive Employees");
        return employeeRepository.findAllInactive();
    }

    @Override
    public void activateEmployee(Long employeeId) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            employee.setActive(true);
            employeeRepository.save(employee);
        } else {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
    }

    @Override
    public void deactivateEmployee(Long employeeId) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            employee.setActive(false);
            employeeRepository.save(employee);
        } else {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
    }

    @Override
    public void deleteEmployee(Long employeeId) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            employeeRepository.delete(employee);
        } else {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
    }
}
