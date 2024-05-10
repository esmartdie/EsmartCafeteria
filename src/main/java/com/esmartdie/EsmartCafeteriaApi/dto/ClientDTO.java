package com.esmartdie.EsmartCafeteriaApi.dto;

import lombok.Data;

@Data
public class ClientDTO {
    private String name;
    private String lastName;
    private String email;
    private String password;
    private boolean active;

    public ClientDTO(String name, String lastName, String email, Boolean active) {
        this.name=name;
        this.lastName=lastName;
        this.email=email;
        this.active=active;
    }
}
