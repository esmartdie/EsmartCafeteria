package com.esmartdie.EsmartCafeteriaApi.service.user;
import com.esmartdie.EsmartCafeteriaApi.dto.ClientDTO;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IClientRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IRoleRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ClientServiceIntegrationTest {

    @Autowired
    private IClientRepository clientRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private IRoleRepository roleRepository;

    private Client client;
    private Role role;


    @BeforeEach
    void setUp() {
        role = new Role (null, "ROLE_USER");
        roleRepository.save(role);
        client = new Client(null, "John", "Doe", "JohnDoe@qa.com", "password", true, role);
        userRepository.save(client);
    }

    @AfterEach
    void tearDown(){
        userRepository.deleteAll();
    }

    @Test
    void getActiveClients() {
        List<ClientDTO> result = clientService.getActiveClients();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getInactiveClients() {
        client.setActive(false);
        userRepository.save(client);

        List<ClientDTO> result = clientService.getInactiveClients();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void updateClientStatus() {
        clientService.updateClientStatus(client.getId(), false);
        Client updatedClient = clientRepository.findById(client.getId()).orElseThrow();
        assertFalse(updatedClient.getActive());
    }

    @Test
    void updateClientsStatus() {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setEmail("JohnDoe@qa.com");
        List<ClientDTO> clientDTOS = Collections.singletonList(clientDTO);

        clientService.updateClientsStatus(clientDTOS, false);

        Client updatedClient = clientRepository.findById(client.getId()).orElseThrow();
        assertFalse(updatedClient.getActive());
    }

    @Test
    void updateClientRating() {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setRating(5);

        clientService.updateClientRating(client.getId(), clientDTO);

        Client updatedClient = clientRepository.findById(client.getId()).orElseThrow();
        assertEquals(5, updatedClient.getRating());
    }
}
