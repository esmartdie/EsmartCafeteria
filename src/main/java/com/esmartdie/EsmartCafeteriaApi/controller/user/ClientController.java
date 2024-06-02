package com.esmartdie.EsmartCafeteriaApi.controller.user;

import com.esmartdie.EsmartCafeteriaApi.dto.ClientDTO;
import com.esmartdie.EsmartCafeteriaApi.service.user.IClientService;
import com.esmartdie.EsmartCafeteriaApi.utils.ValidationGroups;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/moderator/client")
public class ClientController implements IClientController{

    @Autowired
    private IClientService clientService;


    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<List<ClientDTO>> getAllClientsActive() {
        List<ClientDTO> activeClients = clientService.getActiveClients();
        return ResponseEntity.ok(activeClients);

    }

    @GetMapping("/inactive")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<List<ClientDTO>> getAllClientsInactive() {
        List<ClientDTO>inactiveClient = clientService.getInactiveClients();
        return ResponseEntity.ok(inactiveClient);
    }

    @PatchMapping("/{clientId}/updateStatus")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void updateClientStatus(@PathVariable Long clientId, @RequestParam boolean isActive) {

        clientService.updateClientStatus(clientId, isActive);
    }

    @PutMapping("/updateMassiveStatus")
    @Override
    public ResponseEntity<Map<String, String>> updateClientsStatus(@RequestBody List<ClientDTO> clientDTOS,
                                   @RequestParam boolean isActive) {
        Map<String, String> updateResults = clientService.updateClientsStatus(clientDTOS, isActive);
        return ResponseEntity.status(HttpStatus.CREATED).body(updateResults);
    }


    @PatchMapping("/{clientId}/updateRating")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    @Validated
    public void updateClientRating(@PathVariable @Min(value = 1, message = "ID must be greater than 0") Long clientId,
                                   @Validated (ValidationGroups.RatingInfo.class)@RequestBody ClientDTO clientDTO) {

        clientService.updateClientRating(clientId, clientDTO);
    }


}
