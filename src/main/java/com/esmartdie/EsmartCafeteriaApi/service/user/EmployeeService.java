package com.esmartdie.EsmartCafeteriaApi.service.user;


import com.esmartdie.EsmartCafeteriaApi.dto.EmployeeDTO;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IEmployeeRepository;
import com.esmartdie.EsmartCafeteriaApi.exception.ResourceNotFoundException;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
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

    @Override
    public List<EmployeeDTO> getActiveEmployees() {
        log.info("Fetching all active Employees");
        List<Employee> employees = employeeRepository.findAllActive();
        return createEmployeeDTOList(employees);
    }

    private List<EmployeeDTO>  createEmployeeDTOList(List<Employee> employees){
        List<EmployeeDTO> employeeDTOList = new ArrayList<>();

        for(Employee employee : employees){
            EmployeeDTO employeeDTO;
            employeeDTO = new EmployeeDTO();
            employeeDTO.setId(employee.getId());
            employeeDTO.setName(employee.getName());
            employeeDTO.setLastName(employee.getLastName());
            employeeDTO.setEmail(employee.getEmail());
            employeeDTO.setActive(employee.getActive());
            employeeDTO.setEmployee_id(employee.getEmployee_id());

            employeeDTOList.add(employeeDTO);
        }

        return employeeDTOList;
    }

    @Override
    public List<EmployeeDTO> getInactiveEmployees() {
        log.info("Fetching all inactive Employees");
        List<Employee> employees = employeeRepository.findAllInactive();
        return createEmployeeDTOList(employees);
    }

    @Override
    public void updateEmployeeStatus(Long id, boolean isActive) {

        Employee employee = (Employee) userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        employee.setActive(isActive);
        userRepository.save(employee);
    }


    /*
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

     */
}
