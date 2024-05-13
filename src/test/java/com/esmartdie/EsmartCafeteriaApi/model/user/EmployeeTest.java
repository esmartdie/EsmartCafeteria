package com.esmartdie.EsmartCafeteriaApi.model.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeeTest {

    @Test
    void testConstructor() {
        Long id = 1L;
        String name = "bat";
        String lastName = "man";
        String email = "brucewayne@batman.com";
        String password = "password";
        Boolean active = true;
        Role role = new Role(null, "ROLE_MODERATOR");
        Long employeeId = 12345L;

        Employee employee = new Employee(id, name, lastName, email, password, active, role, employeeId);

        assertEquals(id, employee.getId());
        assertEquals(name, employee.getName());
        assertEquals(lastName, employee.getLastName());
        assertEquals(email, employee.getEmail());
        assertEquals(password, employee.getPassword());
        assertEquals(active, employee.getActive());
        assertEquals(employeeId, employee.getEmployee_id());
        assertEquals("ROLE_MODERATOR", employee.getRole().getName());
    }

    @Test
    void testEmployeeIdSetter() {
        Employee employee = new Employee();

        Long employeeId = 54321L;
        employee.setEmployee_id(employeeId);

        assertEquals(employeeId, employee.getEmployee_id());
    }
}