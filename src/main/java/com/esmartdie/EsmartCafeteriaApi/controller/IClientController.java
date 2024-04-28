package com.esmartdie.EsmartCafeteriaApi.controller;

import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface IClientController {
    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    List<Client> getAllActive();

    @GetMapping("/inactive")
    @ResponseStatus(HttpStatus.OK)
    List<Client> getAllInactive();

    @PatchMapping("/{clientId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void activateClient(@PathVariable Long clientId);

    @PatchMapping("/{clientId}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deactivateClient(@PathVariable Long clientId);

    @PatchMapping("/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void activateClients(@RequestBody List<Long> clientIds);

    @PatchMapping("/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void  deactivateClients(@RequestBody List<Long> clientIds);

    @PatchMapping("/{clientId}/updateRating")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateClientRating(@RequestBody Client client);
}
