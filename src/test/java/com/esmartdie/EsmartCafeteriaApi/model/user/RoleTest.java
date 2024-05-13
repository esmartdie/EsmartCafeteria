package com.esmartdie.EsmartCafeteriaApi.model.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoleTest {

    @Test
    void testConstructor() {
        Long id = 1L;
        String name = "ROLE_USER";

        Role role = new Role(id, name);

        assertEquals(id, role.getId());
        assertEquals(name, role.getName());
    }

    @Test
    void testSetterMethods() {
        Role role = new Role();

        Long id = 2L;
        String name = "ROLE_ADMIN";
        role.setId(id);
        role.setName(name);

        assertEquals(id, role.getId());
        assertEquals(name, role.getName());
    }

}