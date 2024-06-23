package com.esmartdie.EsmartCafeteriaApi.service.user;

import com.esmartdie.EsmartCafeteriaApi.dto.ClientDTO;
import com.esmartdie.EsmartCafeteriaApi.exception.ResourceNotFoundException;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IClientRepository;
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
class ClientServiceTest {

    @Mock
    private IClientRepository clientRepository;

    @Mock
    private IUserRepository userRepository;

    @Spy
    private DTOConverter converter;

    @InjectMocks
    private ClientService clientService;

    private Client client;
    private ClientDTO clientDTO;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        client = new Client();
        client.setId(1L);
        client.setEmail("test@example.com");
        client.setActive(true);

        clientDTO = new ClientDTO();
        clientDTO.setEmail("test@example.com");
    }

    @Test
    void getActiveClients() {
        List<Client> clients = Collections.singletonList(client);
        List<ClientDTO> clientDTOs = Collections.singletonList(clientDTO);

        when(clientRepository.findAllActive()).thenReturn(clients);

        List<ClientDTO> result = clientService.getActiveClients();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(clientRepository, times(1)).findAllActive();
    }

    @Test
    void getInactiveClients() {
        client.setActive(false);
        List<Client> clients = Collections.singletonList(client);
        List<ClientDTO> clientDTOs = Collections.singletonList(clientDTO);

        when(clientRepository.findAllInactive()).thenReturn(clients);

        List<ClientDTO> result = clientService.getInactiveClients();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(clientRepository, times(1)).findAllInactive();
    }

    @Test
    void updateClientStatus() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(client));
        when(userRepository.save(any(Client.class))).thenReturn(client);

        clientService.updateClientStatus(1L, false);

        assertFalse(client.getActive());
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(client);
    }

    @Test
    void updateClientStatus_ClientNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> clientService.updateClientStatus(1L, false));

        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(0)).save(any(Client.class));
    }

    @Test
    void updateClientsStatus() {
        List<ClientDTO> clientDTOS = Collections.singletonList(clientDTO);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(client));
        when(userRepository.save(any(Client.class))).thenReturn(client);

        Map<String, String> results = clientService.updateClientsStatus(clientDTOS, false);

        assertNotNull(results);
        assertEquals("Updated successfully", results.get("test@example.com"));
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, times(1)).save(client);
    }

    @Test
    void updateClientsStatus_ClientNotFound() {
        List<ClientDTO> clientDTOS = Collections.singletonList(clientDTO);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        Map<String, String> results = clientService.updateClientsStatus(clientDTOS, false);

        assertNotNull(results);
        assertEquals("Client not found", results.get("test@example.com"));
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, times(0)).save(any(Client.class));
    }

    @Test
    void updateClientRating() {
        clientDTO.setRating(5);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(client));
        when(userRepository.save(any(Client.class))).thenReturn(client);

        Client result = clientService.updateClientRating(1L, clientDTO);

        assertNotNull(result);
        assertEquals(5, result.getRating());
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(client);
    }

    @Test
    void updateClientRating_ClientNotFound() {
        clientDTO.setRating(5);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> clientService.updateClientRating(1L, clientDTO));

        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(0)).save(any(Client.class));
    }
}