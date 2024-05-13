package com.esmartdie.EsmartCafeteriaApi.service.user;

import com.esmartdie.EsmartCafeteriaApi.dto.ClientDTO;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IClientRepository;
import com.esmartdie.EsmartCafeteriaApi.exception.ResourceNotFoundException;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService implements IClientService{

    @Autowired
    private IClientRepository clientRepository;

    @Autowired
    private IUserRepository userRepository;


    @Override
    public List<ClientDTO> getActiveClients() {

        log.info("Fetching all active clients");
        List<Client> clients = clientRepository.findAllActive();
        return createClientDTOList(clients);
    }

    private List<ClientDTO>  createClientDTOList(List<Client> clients){
        List<ClientDTO> clientDTOList = new ArrayList<>();

        for(Client client : clients){
            ClientDTO clientDTO;
            clientDTO = new ClientDTO();
            clientDTO.setName(client.getName());
            clientDTO.setLastName(client.getLastName());
            clientDTO.setEmail(client.getEmail());
            clientDTO.setActive(client.getActive());
            clientDTO.setRating(client.getRating());
            clientDTOList.add(clientDTO);
        }

        return clientDTOList;
    }

    @Override
    public List<ClientDTO>getInactiveClients() {
        log.info("Fetching all inactive clients");
        List<Client> clients = clientRepository.findAllInactive();
        return createClientDTOList(clients);
    }

    @Override
    public void updateClientStatus(Long id, boolean isActive) {

        Client client = (Client) userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        client.setActive(isActive);
        userRepository.save(client);
    }

    @Override
    public Map<String, String> updateClientsStatus(List<ClientDTO> clientDTOS, boolean isActive) {
        Map<String, String> results = new HashMap<>();

        for (ClientDTO dto : clientDTOS) {
            Optional<User> userOptional = userRepository.findByEmail(dto.getEmail());
            if (userOptional.isPresent()) {
                Client client = (Client)userOptional.get();
                client.setActive(isActive);
                userRepository.save(client);
                results.put(dto.getEmail(), "Updated successfully");
            } else {
                results.put(dto.getEmail(), "Client not found");
            }
        }

        return results;
    }


    @Override
    public Client updateClientRating(Long clientId, ClientDTO clientDTO ){

        Client client = (Client) userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));

        client.setRating(clientDTO.getRating());

        userRepository.save(client);

        return client;

    }


}
