package com.esmartdie.EsmartCafeteriaApi.service.user;

import com.esmartdie.EsmartCafeteriaApi.dto.EmployeeResponseDTO;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IEmployeeRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IRoleRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;



import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class EmployeeServiceIntegrationTest {

    @Autowired
    private IEmployeeRepository employeeRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private IRoleRepository roleRepository;

    private Employee employee;

    private Role role;

    @BeforeEach
    void setUp() {
        role=new Role(null, "ROLE_MODERATOR");
        roleRepository.save(role);
        employee = new Employee(null, "John", "Doe", "employee@example.com", "password", true, role, 1234L);
        userRepository.save(employee);
    }

    @Test
    void getActiveEmployees() {
        List<EmployeeResponseDTO> result = employeeService.getActiveEmployees();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getInactiveEmployees() {
        employee.setActive(false);
        userRepository.save(employee);

        List<EmployeeResponseDTO> result = employeeService.getInactiveEmployees();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void updateEmployeeStatus() {
        employeeService.updateEmployeeStatus(employee.getId(), false);

        Employee updatedEmployee = employeeRepository.findById(employee.getId()).orElseThrow();
        assertFalse(updatedEmployee.getActive());
    }

}
