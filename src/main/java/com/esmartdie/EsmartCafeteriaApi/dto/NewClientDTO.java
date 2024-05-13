package com.esmartdie.EsmartCafeteriaApi.dto;

import lombok.Data;

@Data
public class NewClientDTO {
    private String name;
    private String lastName;
    private String email;
    private String password;
    private boolean active;
    public NewClientDTO() {
    }

    public NewClientDTO(String name, String lastName, String email, Boolean active) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.active = active;
    }


    public NewClientDTO(String name, String lastName, String email) {
        this.name=name;
        this.lastName=lastName;
        this.email=email;
    }
}
