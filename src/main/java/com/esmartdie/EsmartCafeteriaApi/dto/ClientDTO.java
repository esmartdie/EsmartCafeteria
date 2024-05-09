package com.esmartdie.EsmartCafeteriaApi.dto;

import lombok.Data;

@Data
public class ClientDTO {
    private String name;
    private String lastName;
    private String email;
    private String password;
    private boolean active;
}
