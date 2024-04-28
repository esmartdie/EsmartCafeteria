package com.esmartdie.EsmartCafeteriaApi.service;

import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface IClientService {

    Optional<List<Client>> getActiveClients();

    Optional<List<Client>> getInactiveClients();

    void activateClient(Long clientId);

    void deactivateClient(Long clientId);

    void activateClients(List<Long> clientIds);

    void deactivateClients(List<Long> clientIds);

    void updateClientRating(Long clientId, double rating);
}
