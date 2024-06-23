package com.esmartdie.EsmartCafeteriaApi.service.user;

import com.esmartdie.EsmartCafeteriaApi.dto.*;
import com.esmartdie.EsmartCafeteriaApi.exception.EmailAlreadyExistsException;
import com.esmartdie.EsmartCafeteriaApi.exception.ResourceNotFoundException;
import com.esmartdie.EsmartCafeteriaApi.exception.UserTypeMismatchException;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IRoleRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import com.esmartdie.EsmartCafeteriaApi.utils.DTOConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IRoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Spy
    private DTOConverter converter;

    private Client client;
    private Employee employee;
    private Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        role = new Role(1L, "ROLE_USER");
        client = new Client(1L, "John", "Doe", "john.doe@example.com", "password123", true, role);
        employee = new Employee(1L, "Jane", "Doe", "jane.doe@example.com", "password123", true, role, 12345L);
    }

    @Test
    void saveUser() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(client);

        User savedUser = userService.saveUser(client);

        assertNotNull(savedUser);
        assertEquals("encodedPassword", savedUser.getPassword());
        verify(userRepository, times(1)).save(client);
    }

    @Test
    void createClientFromDTO() {

        NewClientDTO newClientDTO = new NewClientDTO("John", "Doe", "john.doe@example.com","1234", true);
        Role role = new Role(1L, "ROLE_USER");
        Client client = new Client();
        client.setName("John");
        client.setLastName("Doe");
        client.setEmail("john.doe@example.com");
        client.setPassword("password");
        client.setActive(true);
        client.setRole(role);

        ClientDTO clientDTO = new ClientDTO(1L, "John", "Doe", "john.doe@example.com", true);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(role));
        doReturn(client).when(converter).createClientFromNewClientDTO(any(NewClientDTO.class), any(Role.class));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client savedClient = invocation.getArgument(0);
            savedClient.setId(1L);
            return savedClient;
        });
        doReturn(clientDTO).when(converter).createClientDTOFromClient(any(Client.class));

        ClientDTO result = userService.createClientFromDTO(newClientDTO);

        assertNotNull(result);
        assertEquals(clientDTO, result);

        verify(userRepository).existsByEmail(newClientDTO.getEmail());
        verify(roleRepository).findByName("ROLE_USER");
        verify(converter).createClientFromNewClientDTO(newClientDTO, role);
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(client);
        verify(converter).createClientDTOFromClient(client);
    }

    @Test
    void createEmployeeFromDTO() {

        EmployeeDTO employeeDTO = new EmployeeDTO("Jane", "Doe", "jane.doe@example.com", true, 12345L);
        Role role = new Role(1L, "ROLE_MODERATOR");
        Employee employee = new Employee();
        employee.setName("Jane");
        employee.setLastName("Doe");
        employee.setEmail("jane.doe@example.com");
        employee.setPassword("password");
        employee.setActive(true);
        employee.setRole(role);
        employee.setEmployee_id(12345L);

        EmployeeResponseDTO employeeResponseDTO = new EmployeeResponseDTO(1L, "Jane", "Doe", "jane.doe@example.com", true, 12345L);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(role));
        doReturn(employee).when(converter).createEmployeeFromEmployeeDTO(any(EmployeeDTO.class), any(Role.class));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee savedEmployee = invocation.getArgument(0);
            savedEmployee.setId(1L); // Simulate setting the ID after saving
            return savedEmployee;
        });
        doReturn(employeeResponseDTO).when(converter).createEmployeeResponseDTOFromEmployee(any(Employee.class));

        EmployeeResponseDTO result = userService.createEmployeeFromDTO(employeeDTO);

        assertNotNull(result);
        assertEquals(employeeResponseDTO, result);

        verify(userRepository).existsByEmail(employeeDTO.getEmail());
        verify(roleRepository).findByName("ROLE_MODERATOR");
        verify(converter).createEmployeeFromEmployeeDTO(employeeDTO, role);
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(employee);
        verify(converter).createEmployeeResponseDTOFromEmployee(employee);
    }

    @Test
    void getClientById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(client));

        Client result = userService.getClientById(1L);

        assertNotNull(result);
        assertEquals(client, result);
    }

    @Test
    void getClientById_NotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getClientById(1L));
    }

    @Test
    void getClientById_WrongType() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(employee));

        assertThrows(UserTypeMismatchException.class, () -> userService.getClientById(1L));
    }

    @Test
    void getEmployeeById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(employee));

        Employee result = userService.getEmployeeById(1L);

        assertNotNull(result);
        assertEquals(employee, result);
    }

    @Test
    void getEmployeeById_NotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getEmployeeById(1L));
    }

    @Test
    void getEmployeeById_WrongType() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(client));

        assertThrows(UserTypeMismatchException.class, () -> userService.getEmployeeById(1L));
    }

    @Test
    void updateClientFromDTO() {

        UpdateClientDTO updateClientDTO = new UpdateClientDTO("John", "Smith", "john.smith@example.com");
        Client client = new Client(1L, "Jane", "Doe", "jane.doe@example.com", "password", true, new Role());
        Client updatedClient = new Client(1L, "John", "Smith", "john.smith@example.com", "password", true, new Role());
        ClientDTO clientDTO = new ClientDTO(1L, "John", "Smith", "john.smith@example.com", true);


        when(userRepository.findById(anyLong())).thenReturn(Optional.of(client));
        when(userRepository.save(any(Client.class))).thenReturn(updatedClient);
        doReturn(clientDTO).when(converter).createClientDTOFromClient(any(Client.class));

        ClientDTO result = userService.updateClientFromDTO(1L, updateClientDTO);

        assertNotNull(result);
        assertEquals("John", result.getName());
        assertEquals("Smith", result.getLastName());
        assertEquals("john.smith@example.com", result.getEmail());

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(Client.class));
        verify(converter).createClientDTOFromClient(updatedClient);
    }

    @Test
    void updateClientFromDTO_EmailAlreadyExists() {
        UpdateClientDTO updateClientDTO = new UpdateClientDTO("John", "Smith", "john.smith@example.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(client));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new Client(2L, "Jane", "Doe", "john.smith@example.com", "password123", true, role)));

        assertThrows(EmailAlreadyExistsException.class, () -> userService.updateClientFromDTO(1L, updateClientDTO));
    }
}