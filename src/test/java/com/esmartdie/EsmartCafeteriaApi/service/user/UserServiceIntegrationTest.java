package com.esmartdie.EsmartCafeteriaApi.service.user;

import com.esmartdie.EsmartCafeteriaApi.dto.*;
import com.esmartdie.EsmartCafeteriaApi.exception.EmailAlreadyExistsException;
import com.esmartdie.EsmartCafeteriaApi.exception.ResourceNotFoundException;
import com.esmartdie.EsmartCafeteriaApi.exception.UserTypeMismatchException;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IRoleRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import com.esmartdie.EsmartCafeteriaApi.utils.DTOConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DTOConverter converter;

    private Role userRole;
    private Client client;
    private Employee employee;

    @BeforeEach
    void setUp() {
        userRole = roleRepository.save(new Role(null, "ROLE_USER"));
        client = new Client(null, "John", "Doe", "john.doe@example.com", passwordEncoder.encode("password123"), true, userRole);
        employee = new Employee(null, "Jane", "Doe", "jane.doe@example.com", passwordEncoder.encode("password123"), true, userRole, 12345L);
    }

    @Test
    void createClientFromDTO() {
        NewClientDTO newClientDTO = new NewClientDTO("John", "Doe", "john.doe@example.com","password", true);

        ClientDTO result = userService.createClientFromDTO(newClientDTO);

        assertNotNull(result);
        assertEquals("John", result.getName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertTrue(userRepository.existsByEmail("john.doe@example.com"));
    }

    @Test
    void createEmployeeFromDTO() {
        EmployeeDTO employeeDTO = new EmployeeDTO("Jane", "Doe", "jane.doe@example.com", "password", true, 1234L);

        EmployeeResponseDTO result = userService.createEmployeeFromDTO(employeeDTO);

        assertNotNull(result);
        assertEquals("Jane", result.getName());
        assertEquals("Doe", result.getLastName());
        assertEquals("jane.doe@example.com", result.getEmail());
        assertTrue(userRepository.existsByEmail("jane.doe@example.com"));
    }

    @Test
    void getClientById() {
        client = userRepository.save(client);
        Client foundClient = userService.getClientById(client.getId());

        assertNotNull(foundClient);
        assertEquals(client.getName(), foundClient.getName());
    }

    @Test
    void getClientById_NotFound() {
        assertThrows(ResourceNotFoundException.class, () -> userService.getClientById(999L));
    }

    @Test
    void getClientById_WrongType() {
        employee = userRepository.save(employee);
        assertThrows(UserTypeMismatchException.class, () -> userService.getClientById(employee.getId()));
    }

    @Test
    void getEmployeeById() {
        employee = userRepository.save(employee);
        Employee foundEmployee = userService.getEmployeeById(employee.getId());

        assertNotNull(foundEmployee);
        assertEquals(employee.getName(), foundEmployee.getName());
    }

    @Test
    void getEmployeeById_NotFound() {
        assertThrows(ResourceNotFoundException.class, () -> userService.getEmployeeById(999L));
    }

    @Test
    void getEmployeeById_WrongType() {
        client = userRepository.save(client);
        assertThrows(UserTypeMismatchException.class, () -> userService.getEmployeeById(client.getId()));
    }

    @Test
    void updateClientFromDTO() {
        client = userRepository.save(client);
        UpdateClientDTO updateClientDTO = new UpdateClientDTO("John", "Smith", "john.smith@example.com");

        ClientDTO result = userService.updateClientFromDTO(client.getId(), updateClientDTO);

        assertNotNull(result);
        assertEquals("John", result.getName());
        assertEquals("Smith", result.getLastName());
        assertEquals("john.smith@example.com", result.getEmail());
    }

    @Test
    void checkEmailAvailability() {
        Role userRole = new Role(null, "ROLE_USER");
        roleRepository.save(userRole);

        Client client = new Client(null, "John", "Doe", "john.doe@example.com", "password", true, userRole);
        userRepository.save(client);

        NewClientDTO newClientDTO = new NewClientDTO("Jane", "Doe", "john.doe@example.com","password", true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.createClientFromDTO(newClientDTO));
    }
}
