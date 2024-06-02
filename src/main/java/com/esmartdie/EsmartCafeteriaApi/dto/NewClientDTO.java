package com.esmartdie.EsmartCafeteriaApi.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewClientDTO {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @AssertTrue
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
