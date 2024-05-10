package com.esmartdie.EsmartCafeteriaApi.controller.user;

import com.esmartdie.EsmartCafeteriaApi.dto.ClientDTO;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

public interface IClientController {
    ResponseEntity<List<ClientDTO>> getAllActive();
    ResponseEntity<List<ClientDTO>> getAllInactive();

    void updateClientStatus(@PathVariable Long clientId, @RequestParam boolean isActive);

    ResponseEntity<Map<String, String>> updateClientsStatus(@RequestBody List<ClientDTO> clientDTOS,
                                                           @RequestParam boolean isActive);

    void updateClientRating(@RequestBody Client client);
}
