package com.esmartdie.EsmartCafeteriaApi.controller.user;

import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import com.esmartdie.EsmartCafeteriaApi.service.user.IClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client")
public class ClientController implements IClientController{

    @Autowired
    private IClientService clientService;

    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public List<Client> getAllActive() {
        return clientService.getActiveClients().get();
    }

    @GetMapping("/inactive")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public List<Client> getAllInactive() {
        return clientService.getInactiveClients().get();
    }

    @PatchMapping("/{clientId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void activateClient(@PathVariable Long clientId) {
        clientService.activateClient(clientId);
    }

    @PatchMapping("/{clientId}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void deactivateClient(@PathVariable Long clientId) {
        clientService.deactivateClient(clientId);
    }

    @PatchMapping("/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void activateClients(@RequestBody List<Long> clientIds) {
        clientService.activateClients(clientIds);
    }

    @PatchMapping("/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void  deactivateClients(@RequestBody List<Long> clientIds) {
        clientService.deactivateClients(clientIds);
    }

    @PatchMapping("/{clientId}/updateRating")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void updateClientRating(@RequestBody Client client) {
        clientService.updateClientRating(client.getId(), client.getRating());
    }


}
