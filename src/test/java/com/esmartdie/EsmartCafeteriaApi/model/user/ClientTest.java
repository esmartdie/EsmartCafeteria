package com.esmartdie.EsmartCafeteriaApi.model.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientTest {

    @Test
    void testConstructor() {

        Long id = 1L;
        String name = "Spider";
        String lastName = "Man";
        String email = "spiderman@webtesting.com";
        String password = "password";
        Boolean active = true;
        Role role = new Role(null, "ROLE_USER");

        Client client = new Client(id, name, lastName, email, password, active, role);

        assertEquals(id, client.getId());
        assertEquals(name, client.getName());
        assertEquals(lastName, client.getLastName());
        assertEquals(email, client.getEmail());
        assertEquals(password, client.getPassword());
        assertEquals(active, client.getActive());
        assertEquals(5.0, client.getRating());
        assertEquals("ROLE_USER", client.getRole().getName());
    }

    @Test
    void testRatingSetter() {
        Client client = new Client();
        client.setRating(4.5);
        assertEquals(4.5, client.getRating());
    }

}