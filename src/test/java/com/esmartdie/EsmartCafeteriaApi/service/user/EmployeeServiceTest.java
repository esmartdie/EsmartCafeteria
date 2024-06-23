package com.esmartdie.EsmartCafeteriaApi.service.user;

import com.esmartdie.EsmartCafeteriaApi.dto.EmployeeResponseDTO;
import com.esmartdie.EsmartCafeteriaApi.exception.ResourceNotFoundException;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IEmployeeRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import com.esmartdie.EsmartCafeteriaApi.utils.DTOConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private IEmployeeRepository employeeRepository;

    @Mock
    private IUserRepository userRepository;

    @Spy
    private DTOConverter converter;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;
    private EmployeeResponseDTO employeeResponseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employee = new Employee();
        employee.setId(1L);
        employee.setEmail("employee@example.com");
        employee.setActive(true);

        employeeResponseDTO = new EmployeeResponseDTO();
        employeeResponseDTO.setId(1L);
        employeeResponseDTO.setEmail("employee@example.com");
    }

    @Test
    void getActiveEmployees() {
        List<Employee> employees = Collections.singletonList(employee);
        List<EmployeeResponseDTO> employeeDTOs = Collections.singletonList(employeeResponseDTO);

        when(employeeRepository.findAllActive()).thenReturn(employees);

        List<EmployeeResponseDTO> result = employeeService.getActiveEmployees();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeeRepository, times(1)).findAllActive();
    }

    @Test
    void getInactiveEmployees() {
        employee.setActive(false);
        List<Employee> employees = Collections.singletonList(employee);
        List<EmployeeResponseDTO> employeeDTOs = Collections.singletonList(employeeResponseDTO);

        when(employeeRepository.findAllInactive()).thenReturn(employees);

        List<EmployeeResponseDTO> result = employeeService.getInactiveEmployees();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeeRepository, times(1)).findAllInactive();
    }

    @Test
    void updateEmployeeStatus() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(employee));
        when(userRepository.save(any(Employee.class))).thenReturn(employee);

        employeeService.updateEmployeeStatus(1L, false);

        assertFalse(employee.getActive());
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(employee);
    }

    @Test
    void updateEmployeeStatus_EmployeeNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.updateEmployeeStatus(1L, false));

        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(0)).save(any(Employee.class));
    }
}