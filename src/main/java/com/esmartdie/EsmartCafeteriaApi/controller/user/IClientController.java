package com.esmartdie.EsmartCafeteriaApi.controller.user;

import com.esmartdie.EsmartCafeteriaApi.dto.ClientDTO;
import com.esmartdie.EsmartCafeteriaApi.model.user.Client;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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


    @PatchMapping("/{clientId}/updateRating")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateClientRating(@PathVariable @Min(value = 1, message = "ID must be greater than 0") Long id,
                            @Valid @RequestBody ClientDTO clientDTO);
}
