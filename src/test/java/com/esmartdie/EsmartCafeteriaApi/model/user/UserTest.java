package com.esmartdie.EsmartCafeteriaApi.model.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Harry");
        user.setLastName("Potter");
        user.setEmail("hpotter@testing.com");
        user.setPassword("password");
        user.setActive(true);
        user.setRole(role);
    }

    @Test
    void testGetters() {
        assertEquals(1L, user.getId());
        assertEquals("Harry", user.getName());
        assertEquals("Potter", user.getLastName());
        assertEquals("hpotter@testing.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals(true, user.getActive());
        assertEquals(role, user.getRole());
    }

    @Test
    void testSetters() {
        Role newRole = new Role();
        user.setId(2L);
        user.setName("Homer J");
        user.setLastName("Simpsons");
        user.setEmail("hs@nucleartesting.com");
        user.setPassword("newPassword");
        user.setActive(false);
        user.setRole(newRole);

        assertEquals(2L, user.getId());
        assertEquals("Homer J", user.getName());
        assertEquals("Simpsons", user.getLastName());
        assertEquals("hs@nucleartesting.com", user.getEmail());
        assertEquals("newPassword", user.getPassword());
        assertEquals(false, user.getActive());
        assertEquals(newRole, user.getRole());
    }

}