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

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */

    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public List<Client> getAllActive() {
        return clientService.getActiveClients().get();
    }

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */

    @GetMapping("/inactive")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public List<Client> getAllInactive() {
        return clientService.getInactiveClients().get();
    }

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */

    @PatchMapping("/{clientId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void activateClient(@PathVariable Long clientId) {
        clientService.activateClient(clientId);
    }

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */

    @PatchMapping("/{clientId}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void deactivateClient(@PathVariable Long clientId) {
        clientService.deactivateClient(clientId);
    }

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */

    @PatchMapping("/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void activateClients(@RequestBody List<Long> clientIds) {
        clientService.activateClients(clientIds);
    }

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */

    @PatchMapping("/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void  deactivateClients(@RequestBody List<Long> clientIds) {
        clientService.deactivateClients(clientIds);
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
