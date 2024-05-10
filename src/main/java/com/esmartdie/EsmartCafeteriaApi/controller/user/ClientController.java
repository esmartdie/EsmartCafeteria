package com.esmartdie.EsmartCafeteriaApi.controller.user;

import com.esmartdie.EsmartCafeteriaApi.dto.ClientDTO;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.service.user.IClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/client")
public class ClientController implements IClientController{

    @Autowired
    private IClientService clientService;


    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<List<ClientDTO>> getAllActive() {
        List<ClientDTO> activeClients = clientService.getActiveClients();
        return ResponseEntity.ok(activeClients);

    }


    @GetMapping("/inactive")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<List<ClientDTO>> getAllInactive() {
        List<ClientDTO>inactiveClient = clientService.getInactiveClients();
        return ResponseEntity.ok(inactiveClient);
    }

    @PatchMapping("/{clientId}/updateStatus")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void updateClientStatus(@PathVariable Long clientId, @RequestParam boolean isActive) {

        clientService.updateClientStatus(clientId, isActive);
    }


    @PatchMapping("/updateMassiveStatus")
    @Override
    public ResponseEntity<Map<String, String>> updateClientsStatus(@RequestBody List<ClientDTO> clientDTOS,
                                   @RequestParam boolean isActive) {
        Map<String, String> updateResults = clientService.updateClientStatus(clientDTOS, isActive);
        return ResponseEntity.ok(updateResults);
    }


    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */

    @PatchMapping("/{clientId}/updateRating")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void updateClientRating(@RequestBody Client client) {
        clientService.updateClientRating(client.getId(), client.getRating());
    }


}
