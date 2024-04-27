package com.esmartdie.EsmartCafeteriaApi.controller;

import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import com.esmartdie.EsmartCafeteriaApi.service.IClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    @Autowired
    private IClientService clientService;

    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    public List<Client> getAllActive() {
        return clientService.getActiveClients().get();
    }

    @GetMapping("/inactive")
    @ResponseStatus(HttpStatus.OK)
    public List<Client> getAllInactive() {
        return clientService.getInactiveClients().get();
    }

    @PatchMapping("/{clientId}/activate")
    public ResponseEntity<String> activateClient(@PathVariable Long clientId) {
        clientService.activateClient(clientId);
        return ResponseEntity.ok("Client activated successfully");
    }

    @PatchMapping("/{clientId}/deactivate")
    public ResponseEntity<String> deactivateClient(@PathVariable Long clientId) {
        clientService.deactivateClient(clientId);
        return ResponseEntity.ok("Client deactivated successfully");
    }

    @PatchMapping("/activate")
    public ResponseEntity<String> activateClients(@RequestBody List<Long> clientIds) {
        clientService.activateClients(clientIds);
        return ResponseEntity.ok("Clients activated successfully");
    }

    @PatchMapping("/deactivate")
    public ResponseEntity<String> deactivateClients(@RequestBody List<Long> clientIds) {
        clientService.deactivateClients(clientIds);
        return ResponseEntity.ok("Clients deactivated successfully");
    }


}
