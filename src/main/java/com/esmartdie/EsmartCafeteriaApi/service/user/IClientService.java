package com.esmartdie.EsmartCafeteriaApi.service.user;

import com.esmartdie.EsmartCafeteriaApi.dto.ClientDTO;

import java.util.List;
import java.util.Map;

public interface IClientService {

    List<ClientDTO> getActiveClients();

    List<ClientDTO> getInactiveClients();


    void updateClientStatus(Long id, boolean isActive);


    Map<String, String> updateClientStatus(List<ClientDTO> clientDTOS, boolean isActive);

    void updateClientRating(Long clientId, double rating);
}
