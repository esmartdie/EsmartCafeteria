package com.esmartdie.EsmartCafeteriaApi.repository.user;

import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IEmployeeRepositoryTest {

    @Autowired
    private IEmployeeRepository employeeRepository;
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    private User user;
    private Employee employee;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        roleRepository.save(role);
        user = new User(1L, "Eren", "Jaeger", "erenJ@titantesting.com", "password", true, role);
        userRepository.save(user);

        employee = new Employee(2L, "Armin", "Arlert", "ArminA@titantesting.com", "password", true, role, 1L);
        employeeRepository.save(employee);
    }

    @AfterEach
    void tearDown() {
        employeeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testFindAll() {
        List<Employee> employees = employeeRepository.findAll();
        assertEquals(1, employees.size());
    }

    @Test
    void testFindAllActive() {
        List<Employee> employees = employeeRepository.findAllActive();
        assertEquals(1, employees.size());
        assertTrue(employees.get(0).getActive());
    }

    @Test
    void testFindAllInactive() {
        List<Employee> employees = employeeRepository.findAllInactive();
        assertEquals(0, employees.size());
    }

    @Test
    void testFindByName_ClientFound() {
        User foundUser = userRepository.findByName("Armin");
        assertNotNull(foundUser);
        assertEquals("Armin", foundUser.getName());
    }


    @Test
    void testFindByEmail_ClientFound() {
        Optional<User> foundUserOptional = userRepository.findByEmail("ArminA@titantesting.com");
        assertTrue(foundUserOptional.isPresent());
        assertEquals("ArminA@titantesting.com", foundUserOptional.get().getEmail());
    }

    @Test
    void testFindById_ClientFound() {
        Optional<User> foundUserOptional = userRepository.findById(employee.getId());
        assertTrue(foundUserOptional.isPresent());
        assertEquals(employee.getId(), foundUserOptional.get().getId());
    }

}