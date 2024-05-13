package com.esmartdie.EsmartCafeteriaApi.repository.user;

import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
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
class IClientRepositoryTest {

    @Autowired
    private IClientRepository clientRepository;
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    private User user;
    private Client client;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        roleRepository.save(role);
        user = new User(1L, "Eren", "Jaeger", "erenJ@titantesting.com", "password", true, role);
        userRepository.save(user);

        client = new Client(2L, "Mikasa", "Ackerman", "mikasaA@titantesting.com", "password", true, role);
        clientRepository.save(client);
    }

    @AfterEach
    void tearDown() {
        clientRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testFindAll() {
        List<Client> clients = clientRepository.findAll();
        assertEquals(1, clients.size());
    }

    @Test
    void testFindAllActive() {
        List<Client> clients = clientRepository.findAllActive();
        assertEquals(1, clients.size());
        assertTrue(clients.get(0).getActive());
    }

    @Test
    void testFindAllInactive() {
        List<Client> clients = clientRepository.findAllInactive();
        assertEquals(0, clients.size());
    }

    @Test
    void testFindByName_ClientFound() {
        User foundUser = userRepository.findByName("Mikasa");
        assertNotNull(foundUser);
        assertEquals("Mikasa", foundUser.getName());
    }


    @Test
    void testFindByEmail_ClientFound() {
        Optional<User> foundUserOptional = userRepository.findByEmail("mikasaA@titantesting.com");
        assertTrue(foundUserOptional.isPresent());
        assertEquals("mikasaA@titantesting.com", foundUserOptional.get().getEmail());
    }

    @Test
    void testFindById_ClientFound() {
        Optional<User> foundUserOptional = userRepository.findById(client.getId());
        assertTrue(foundUserOptional.isPresent());
        assertEquals(client.getId(), foundUserOptional.get().getId());
    }

}