package com.esmartdie.EsmartCafeteriaApi.service.user;

import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IClientRepository;
import com.esmartdie.EsmartCafeteriaApi.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService implements IClientService{

    @Autowired
    private IClientRepository clientRepository;


    @Override
    public Optional<List<Client>> getActiveClients() {
        log.info("Fetching all active clients");
        return clientRepository.findAllActive();
    }

    @Override
    public Optional<List<Client>> getInactiveClients() {
        log.info("Fetching all inactive clients");
        return clientRepository.findAllInactive();
    }

    @Override
    public void activateClient(Long clientId) {
        Optional<Client> optionalClient = clientRepository.findById(clientId);
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            client.setActive(true);
            clientRepository.save(client);
        } else {
            throw new ResourceNotFoundException("Client not found with id: " + clientId);
        }
    }

    @Override
    public void deactivateClient(Long clientId) {
        Optional<Client> optionalClient = clientRepository.findById(clientId);
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            client.setActive(false);
            clientRepository.save(client);
        } else {
            throw new ResourceNotFoundException("Client not found with id: " + clientId);
        }

    }

    @Override
    public void activateClients(List<Long> clientIds) {
        List<Client> clients = clientRepository.findAllById(clientIds);
        for (Client client : clients) {
            client.setActive(true);
        }
        clientRepository.saveAll(clients);
    }

    @Override
    public void deactivateClients(List<Long> clientIds) {
        List<Client> clients = clientRepository.findAllById(clientIds);
        for (Client client : clients) {
            client.setActive(false);
        }
        clientRepository.saveAll(clients);
    }

    @Override
    public void updateClientRating(Long clientId, double rating){
        Optional<Client> optionalClient = clientRepository.findById(clientId);
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            client.setRating(rating);
            clientRepository.save(client);
        } else {
            throw new ResourceNotFoundException("Client not found with id: " + clientId);
        }

    }


}
