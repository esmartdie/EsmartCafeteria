package com.esmartdie.EsmartCafeteriaApi.service.user;

import com.esmartdie.EsmartCafeteriaApi.dto.ClientDTO;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;

import java.util.List;
import java.util.Map;

public interface IClientService {

    List<ClientDTO> getActiveClients();

    List<ClientDTO> getInactiveClients();

    void updateClientStatus(Long id, boolean isActive);

    Map<String, String> updateClientsStatus(List<ClientDTO> clientDTOS, boolean isActive);

    Client updateClientRating(Long clientId, ClientDTO clientDTO);
}
